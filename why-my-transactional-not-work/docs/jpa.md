# JPA - 영속성 컨텍스트와 트랜잭션

## 1:N 관계를 Fetch join 해보자

Team, Member 클래스가 정의되어 있고, 이들은 1:N 연관관계를 가집니다. 코드는 아래와 같습니다.

```java
// Team.java
@Setter
@Getter
@NoArgsConstructor
@Entity
public class Team {
    @Id
    @Column(name = "TEAM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
```

```java
// Member.java
@Setter
@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public Member(String name) {
        this.name = name;
    }

    public Member(String name, Team team) {
        this.name = name;
        this.team = team;
    }
}
```

Team을 조회하면서, 해당 팀에 속한 모든 멤버 객체를 함께 조회하고자 합니다. Data JPA를 상속받은 레포지토리를 만들고,  findByIdWithAllMembers()라는 메서드를 정의합니다. Team과 이에 속하는 Member를 **함께** 조회할 것이기 때문에, fetch join을 사용합니다.

```java
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t join fetch t.members where t.id = :id")
    Optional<Team> findByIdWithAllMembers(Long id);
}
```

이제 조회 메서드가 잘 동작하는지 테스트를 해봅니다. 

- given : teamA라는 이름을 가진 Team 객체와, teamA에 속한 10개의 Member 객체를 DB에 저장합니다.
- when : findByIdWithAllMembers() 메서드를 호출합니다.
- then : 기대하는 결과는 Team과 List<Member> members에 10명의 멤버가 함께 로딩되는 것입니다.

JUnit 테스트 메서드를 다음과 같이 작성하고 실행합니다. 

```java
@Transactional
@Test
void transactionalFetchJoin() {
    Team team = new Team("teamA");
    Team saved = teamRepository.save(team);

    for (int i = 0; i < 10; i++) {
        Member member = new Member("member" + String.valueOf(i), team);
        memberRepository.save(member);
    }

    System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
    Team t = teamRepository.findByIdWithAllMembers(saved.getId())
            .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

    assertThat(t.getMembers().size()).isEqualTo(10);
}
```

`expected: 10 but was: 0` 라는 메시지와 함께 **테스트가 실패합니다 ...** Fetch join이 잘 적용되었는지를 확인하기 위해, 쿼리 로그를 들여다봅시다.

```sql
----------------------------------
// INSERT TEAM
insert 
  into
      team
      (team_id, name) 
  values
      (default, ?)
----------------------------------- 
// INSERT MEMBER X 10
insert 
  into
      member
      (member_id, name, team_id) 
  values
      (default, ?, ?)
... 
----------------------------------- 
// SELECT TEAM, MEMBER
select
    team0_.team_id as team_id1_1_0_,
    members1_.member_id as member_i1_0_1_,
    team0_.name as name2_1_0_,
    members1_.name as name2_0_1_,
    members1_.team_id as team_id3_0_1_,
    members1_.team_id as team_id3_0_0__,
    members1_.member_id as member_i1_0_0__ 
from
    team team0_ 
inner join
    member members1_ 
        on team0_.team_id=members1_.team_id 
where
    team0_.team_id=?
```

insert query가 출력되었지만, 실제 DB에는 아무 값도 저장되지 않습니다. 테스트 메서드에 @Transactional이 붙었기 때문에 메서드 종료 후 롤백될 것입니다. 엔티티가 DB에 잘 저장되었는지 확인이 어렵습니다.

우선 find 부분의 쿼리 내용만 보자면, team_id, member_id, name을 모두 select 하고 있는 것을 보아, Fetch join에 대한 쿼리가 잘 적용이 된 것을 볼 수 있습니다. 

이번에는 테스트 코드 정의 위의 @Transactional 애노테이션을 제거해봅니다.

```java
// @Transactional <- 제거
@Test
void nonTransactionalFetchJoin() {
    Team team = new Team("teamA");
    Team saved = teamRepository.save(team);

    for (int i = 0; i < 10; i++) {
        Member member = new Member("member" + String.valueOf(i), team);
        memberRepository.save(member);
    }

    System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
    Team t = teamRepository.findByIdWithAllMembers(saved.getId())
            .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

    assertThat(t.getMembers().size()).isEqualTo(10);
}
```

**테스트 코드가 성공합니다!** 특이한 점은, 쿼리 로그는 달라진 게 없는 것 같습니다. 또한 매 save() 호출마다 DB에 엔티티가 저장되고 있습니다.

List<Member>를 로드하려면 @Transactional을 빼버리면 되는 걸까요? 만약 이게 테스트 메서드가 아니라 비즈니스 서비스였다면, 예외 시 롤백이 이뤄지지 않는 문제가 있습니다. 그렇다면, 첫 번째 테스트에서는 왜 List<Member>를 가져올 수 없었을까요? 트랜잭션을 보장하면서 Fetch join 할 방법은 없는 걸까요?

## 엔티티 매니저와 영속성 컨텍스트

### 엔티티 매니저와 영속성 컨텍스트란?

JPA의 Hibernate 구현체는 **엔티티 매니저**가 엔티티 저장, 수정, 삭제, 조회를 관리합니다. [자바 공식 문서](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)에서 설명하는 정의를 살펴보겠습니다.

> Interface used to interact with the persistence context.
>  
> An `EntityManager` instance is associated with a persistence context. A persistence context is a set of entity instances in which for any persistent entity identity there is a unique entity instance. Within the persistence context, the entity instances and their lifecycle are managed. The `EntityManager` API is used to create and remove persistent entity instances, to find entities by their primary key, and to query over entities.
>  
> The set of entities that can be managed by a given `EntityManager` instance is defined by a persistence unit. A persistence unit defines the set of all classes that are related or grouped by the application, and which must be colocated in their mapping to a single database.


요약하자면, 영속성 컨텍스트와 상호작용하는 인터페이스입니다. 엔티티 매니저를 통해 영속성 컨텍스트에 엔티티를 저장하거나 삭제하고, 엔티티 ID로 엔티티 인스턴스를 쿼리할 수 있습니다. 

영속성 컨텍스트란, 애플리케이션과 DB 사이의 1차 캐시 역할을 하는 저장소입니다. 한 번의 트랜잭션을 lifecycle으로 갖는다는 특징이 있습니다. 캐싱된 엔티티의 변경 사항을 추적하는 기능도 합니다. 트랜잭션 안에서 엔티티가 변경된다면, 엔티티 상태를  ‘Dirty’ 라고 마킹한 후, 트랜잭션 커밋 시 더티 엔티티의 변경 사항을 DB에 반영합니다. 이를 **Dirty checking** 이라는 용어로 표현합니다.

### 엔티티 매니저의 엔티티 관리

![image](https://user-images.githubusercontent.com/74892010/161395059-acb2059d-573b-4088-bc5a-bb9985e80df1.png)

위 그림은 엔티티 매니저가 관리하는 엔티티의 lifecycle을 보여줍니다.[김영한] 다음은 각각의 영속 상태에 대한 설명입니다.

- New : 영속성 컨텍스트와 엔티티가 관계 없는 비영속 상태
- Managed : 영속성 컨텍스트가 엔티티를 관리하는 영속 상태 (엔티티가 캐싱된 상태)
- Detached : 영속성 컨텍스트에서 관리되던 엔티티가 분리된 준영속 상태
- Removed : 엔티티가 영속성 컨텍스트에서 삭제된 상태
- DB : DB에 저장된 상태

중요한 점은 flush()를 제외한 엔티티 매니저의 메서드들은 협력 대상이 DB가 아닌 영속성 컨텍스트라는 점입니다. 엔티티의 변경 내용이 실질적으로 DB에 반영되는 것은 flush()가 이루어지는 경우입니다. 

### Flush

영속성 컨텍스트 내부에는 “쓰기 지연 SQL 저장소" 가 존재합니다. 쿼리를 최대한 늦게 보내서, 불필요한 쿼리를 최소화하기 위함입니다. 엔티티 저장/수정 사항이 생기면, 영속성 컨텍스트는 쓰기 지연 SQL 저장소에 쿼리를 저장합니다. 이후, flush()와 함께 DB에 쿼리가 전달됩니다.

- 이러한 behavior는 `transactional write-behind`로 더 잘 알려져 있는 것 같습니다.
- transactional write-behind cache라는 용어가 사용되는 것을 확인했으나, Hibernate 에서도 공식적으로 이렇게 표현하는지는 확실하지 않습니다.

즉, Flush는 저장된 SQL을 DB에 반영하는 작업을 의미합니다.

flush()가 호출되는 시점은 다음의 3가지 경우입니다.

- flush()를 명시적으로 호출할 때
- Transaction commit이 발생할 때
    - 쓰기 지연 SQL 저장소에 저장된 엔티티 변경 사항이 플러시되지 않고 트랜잭션이 커밋되는 것은 무의미하므로, 이러한 문제를 예방하기 위해 JPA가 flush()를 자동으로 호출
- JPQL이 실행되기 전
    - JPQL을 통한 조회는 영속성 컨텍스트의 1차 캐시를 찾지 않고, JPQL을 SQL로 변환해 DB에 전달함
    - 엔티티 변경 내역이 1차 캐시에만 저장되어 있을 때, JPQL로 직접 DB에 접근하면 엔티티 조회가 안되는 문제 등을 예방
    - 단, 엔티티 매니저의 플러시 모드를 FlushModeType.COMMIT으로 변경하면, 쿼리 실행 시 플러시되지 않게 할 수 있음

## 엔티티 매니저가 엔티티를 영속성 컨텍스트에 저장/조회하는 절차

EntityManager를 사용한 JPA의 엔티티 저장/조회 절차를 알아보겠습니다.

### 엔티티 저장

JPA에 정의된 엔티티 저장 메서드는 persist() 입니다. 엔티티 객체를 인자로 사용합니다.

1. persist()가 호출되면 영속성 컨텍스트에 엔티티가 캐싱된다.
2. 영속성 컨텍스트의 쓰기 지연 SQL 저장소에 INSERT 쿼리가 저장된다.
3. flush()와 함께 쓰기 지연 SQL 저장소의 쿼리를 내보내고 DB에 변경 사항을 반영한다.

### 엔티티 조회

JPA에 정의된 엔티티 조회 메서드는 find() 입니다. 엔티티의 클래스 타입과 엔티티 ID를 조회 키로 사용합니다. 참고할 만한 점은, find() 메서드를 통해 조회하는지, 커스텀 JPQL을 통해 조회하는지에 따라 조회 절차가 다르다는 점입니다.

1. find() 메서드를 통한 엔티티 조회
    1. 엔티티 ID를 키로 사용해 영속성 컨텍스트에서 엔티티를 조회합니다.
        1. 엔티티를 찾았다면 이를 그대로 반환합니다.
    2. 엔티티를 찾지 못했다면 DB에서 엔티티를 조회합니다.
    3. 조회한 엔티티를 영속성 컨텍스트에 캐싱합니다.
    4. 이때 영속화된 엔티티를 반환합니다.
2. JPQL로부터 엔티티 조회
    1. JPQL이 SQL로 변환되어 DB를 조회합니다.
    2. 조회한 엔티티를 영속성 컨텍스트에 캐싱을 시도합니다.
        1. 이때 이미 해당 엔티티가 있다면, 조회한 내용은 버리고 영속성 컨텍스트에 캐싱된 엔티티를 반환합니다.
        2. 해당 엔티티가 없다면 조회한 엔티티를 영속성 컨텍스트에 캐싱하고, 영속화된 엔티티를 반환합니다.

두 경우의 공통점은, 반환하는 엔티티가 1차 캐시에 저장된 영속화된 엔티티라는 점입니다. 영속성 컨텍스트에서 관리되는 엔티티는 동일성이 보장됩니다.

다시 원래 문제로 되돌아가보겠습니다. 우리는 Data JPA를 사용해 Team, Member 객체에 대해 save() 메서드를 호출했습니다. 이후 객체들은 모두 영속화되어, 영속성 컨텍스트에서 관리될 것입니다.

또한, JPQL로 Fetch join을 시도했을 때, 실행된 SQL 쿼리에서 Team과 Member의 정보를 함께 가져오라는 select 쿼리 또한 확인했습니다. DB에 실제로 접근 조회가 이루졌을 것입니다.

2.2.1 로 미루어보면, DB에서 조회한 내용을 버리고 영속성 컨텍스트에 캐싱된 엔티티를 반환했다는 점을 짐작할 수 있습니다. 

## 과연 그럴까요 ?

지금까지 JPA의 동작 원리를 살펴보았고, 그럴듯한 문제의 원인도 유추할 수 있었습니다. 하지만 해결했다기엔 아직 찜찜한 부분들이 있습니다...

문제의 상황은 Pure JPA가 아닌 Spring Data JPA의 JpaRepository를 상속받은 경우입니다. 실제로 save() 메서드가 어떻게 구현되었는지 장담할 수 없습니다. 또한 @Transactional을 제외했을때는 왜 List<Member>를 가져오는 데 성공했는지를 설명하기 어렵습니다.

무엇보다도, 트랜잭션을 보장하면서 List<Member>를 함께 가져오는 방법을 찾지 못했습니다.

### SimpleJpaRepository : Spring Data JPA가 사용하는 구현체

```java
public interface TeamRepository extends JpaRepository<Team, Long> {
}
```

위 코드는 Spring Data JPA의 기능을  상속받는 코드입니다. 텅 빈 껍데기 같지만, 커스텀 구현을 따로 정의하지 않아도 Data JPA가 제공하는 CRUD 메서드를 사용할 수 있습니다. 어딘가에 CRUD 메서드를 실제로 정의한 구현체가 있을 것입니다.

![image](https://user-images.githubusercontent.com/74892010/161395080-433e8514-d309-42f4-a0d8-5016aab6f134.png)

위 그림은 TeamRepository의 상속 관계 다이어그램을 보여줍니다. TeamRepository는 JpaRepository를 확장하고 있으며, JpaRepository를 구현하는 것은 SimpleJpaRepository임을 알 수 있습니다. 

SimpleJpaRepository 코드를 들여다보면 자주 사용하는 save(), findById(), findAll() 등의 메서드가 어떻게 구현되어있는지 확인할 수 있을 것입니다.

- 참고로, IntelliJ의 맥북 커맨드 기준으로 Shift를 연속 2번 누르면 검색 창이 뜨는데, SimpleJpaRepository를 입력하면 구현 코드를 쉽게 찾을 수 있습니다.

SimpleJpaRepository.save() 메서드는 아래와 같이 구현되어 있습니다. 

```java
// SimpleJpaRepository.java
@Transactional
@Override
public <S extends T> S save(S entity) {

    Assert.notNull(entity, "Entity must not be null.");

    if (entityInformation.isNew(entity)) { // Returns whether the given entity is considered to be new.
        em.persist(entity);
        return entity;
    } else {
        return em.merge(entity);
    }
}
```

isNew() 메서드의 설명을 보면, 엔티티가 새로운 객체인지를 판단한다고 합니다. 새로운 객체라면, persist()가 호출되고, 영속화된 객체를 반환합니다. 

그렇지 않다면 merge()가 호출되고 그 결과를 반환합니다.

여기서 merge()란, 새로운 영속 상태의 엔티티를 반환하는 메서드입니다. 영속성 컨텍스트에서 ID로 조회가 가능한 엔티티라면 엔티티를 새로 영속화한 후 반환합니다. 비영속/준영속 상태인 엔티티라면 DB에서 조회한 후 새로 영속화를 시도합니다. DB에도 없다면 새로 엔티티를 생성한 후 영속화합니다. 즉, save or update를 수행하는 것입니다.

- save()는 동일성이 보장됩니다.

또 한가지 중요한 점은, 메서드 레벨 @Transactional 애노테이션이 붙어있다는 점입니다. save() 메서드의 결과가 반환되면서 트랜잭션이 커밋될 것이며, flush()가 호출될 것입니다.

## 다시 테스트 코드로...

다시 테스트 코드를 찬찬히 분석해보겠습니다. 두 테스트 메서드의 로직은 완전히 동일합니다. 차이점은 메서드 이름과 메서드 위의 @Transactional 애노테이션 유무입니다. 편의상 transactionalFetchJoin(), nonTransactionalFetchJoin()으로 설명하겠습니다. 

```java
@Transactional
@Test
void transactionalFetchJoin() {
    Team team = new Team("teamA");
    Team saved = teamRepository.save(team);

    for (int i = 0; i < 10; i++) {
        Member member = new Member("member" + String.valueOf(i), team);
        memberRepository.save(member);
    }

    System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
    Team t = teamRepository.findByIdWithAllMembers(saved.getId())
            .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

    assertThat(t.getMembers().size()).isEqualTo(10); // fail
}

@Test
void nonTransactionalFetchJoin() {
    Team team = new Team("teamA");
    Team saved = teamRepository.save(team);

    for (int i = 0; i < 10; i++) {
        Member member = new Member("member" + String.valueOf(i), team);
        memberRepository.save(member);
    }

    System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
    Team t = teamRepository.findByIdWithAllMembers(saved.getId())
            .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

    assertThat(t.getMembers().size()).isEqualTo(10); // success
}
```

save() 메서드 레벨에서 @Transactional 애노테이션이 설정되어있다는 점을 고려하면, transactionalFetchJoin()은 중복된 @Transactional 애노테이션이 붙어있는 셈입니다.

반대로, nonTransactionalFetchJoin()은 teamRepository.save(), memberRepository.save()가 개별적인 트랜잭션으로 동작할 것입니다. 

두 메서드의 assertThat에 breakpoint를 걸고 디버그 모드로 실행한 후, 차이점을 비교해보겠습니다.

![image](https://user-images.githubusercontent.com/74892010/161395097-4268d67a-717f-4f6d-bb2c-560f1a8de8e3.png)
  
먼저 nonTransactionalFetchJoin() 입니다. 코드라인 59, 60 옆에 team 객체의 레퍼런스를 확인할 수 있습니다. save() 메서드는 동일성을 보장하므로, team, saved가 같은 레퍼런스인 Team@10596으로 표현됩니다. 하지만 코드라인 68을 보면, Team t 객체는 Team@10597으로, 다른 레퍼런스임을 볼 수 있습니다. 이는 데이터 동일성을 만족하지 않는다는 의미입니다. 

앞서 영속성 컨텍스트에서 관리되는 엔티티는 동일성이 보장된다고 하였습니다. 또한, 영속성 컨텍스트의 lifecycle은 한번의 트랜잭션 범위입니다.

save()와 findByIdWithAllMembers() 메서드는 서로 다른 트랜잭션에서 실행되기 때문에, 영속성 컨텍스트에서 관리되던 객체가 아닌 새로운 객체를 찾은 것입니다.

세부 동작은 다음과 같습니다.

- line 60
    - Team 영속화 후 반환
    - flush() 호출 및 DB 반영
    - 트랜잭션 커밋
    - 영속성 컨텍스트 종료, Team 객체 detach
- line 64
    - Member 영속화 후 반환
    - flush() 호출 및 DB 반영
    - 트랜잭션 커밋
    - 영속성 컨텍스트 종료, Member 객체 detach
- line 68
    - Team과 Member 객체를 DB에서 조회
    - 영속성 컨텍스트에 Team 엔티티가 없으므로, Member 연관 정보와 함께 Team 엔티티를 캐싱
    - Team 객체 반환

![image](https://user-images.githubusercontent.com/74892010/161395103-dd70dacb-ce50-435b-85c4-97ea882433c8.png)

이번에는 transactionalFetchJoin()입니다. 앞선 nonTransactionalFetchJoin()과 다르게, team, saved, t 객체가 모두 같은 레퍼런스 Team@10574임을 볼 수 있습니다. 이들이 모두 같은 트랜잭션 바운더리에서 실행되며, findByIdWithAllMembers() 메서드의 결과는 영속성 컨텍스트에서 관리되는 객체를 찾은 것임을 알 수 있습니다. 

세부 동작은 다음과 같습니다.

- line 43
    - Team 영속화 후 반환
- line 46
    - Member 영속화 후 반환
- line 51
    - Team과 Member 객체를 DB에서 조회
    - 영속성 컨텍스트에 Team 엔티티가 있으므로, 조회한 결과를 버림
    - 영속성 컨텍스트의 Team 객체 반환

앞서 transactionalFetchJoin()은 중복되는 @Transactional 애노테이션이 설정되어있다고 하였습니다. 그렇다면, 트랜잭션 바운더리는 어떻게 설정되었던 걸까요?

### Spring @Transactional propagation policy

[Spring 공식 문서](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Propagation.html)에 따르면, @Transactional 애노테이션은 다음과 같은 Propagation policy가 존재합니다. 이들 중 REQUIRED가 디폴트 설정입니다.

- REQUIRED : 기존 트랜잭션을 지원하며, 없다면 새 트랜잭션을 생성한다.
- REQUIRED_NEW : 새 트랜잭션을 생성하며, 기존 트랜잭션은 일시 중단한다.
- MANDATORY : 기존 트랜잭션을 지원하며, 없다면 예외를 던진다.
- NESTED : 기존 트랜잭션이 있다면 중첩 트랜잭션을 실행하고, 없다면 REQUIRED 처럼 동작한다.
- NEVER : 트랜잭션 없이 실행하며, 기존 트랜잭션이 있다면 예외를 던진다.
- SUPPORTS : 기존 트랜잭션을 지원하며, 트랜잭션이 없다면 트랜잭션 없이 실행한다.
- NOT_SUPPORTED : 트랜잭션 없이 실행하며, 기존 트랜잭션이 있다면 일시 중단한다.

이에 따르면, TeamRepository의 메서드들은 테스트 메서드의 트랜잭션에 합류했다는 것을 알 수 있습니다.

## 트랜잭션과 함께 List<Member>에 접근하려면?

단 한 줄의 코드를 추가하면 됩니다. member를 save()하면서, team의 List<Member>에 해당 멤버를 add 합니다. 단지 순수하게 객체 간의 연관 관계를 맺어주는 것입니다. 

이렇게 하면 영속성 컨텍스트가 생존하더라도 List<Member>에 접근할 수 있게 됩니다.

```java
for (int i = 0; i < 10; i++) {
    Member member = new Member("member" + String.valueOf(i), team);
    memberRepository.save(member);
    team.getMembers().add(member); // <- 추가됨
}
```

물론, 이 add operation으로 인한 Team의 변경 사항이  DB에 반영되지는 않습니다. MEMBER 테이블에서 TEAM_ID를 FK로 관리하고 있기 때문입니다. 다시 말하면, Member와 Team의 연관관계에서, 연관관계의 주인은 Member.team이기 때문에, Member.team의 변경 사항이 있어야 DB에 반영됩니다. 

이런 점에서 add operation은 자칫 불필요해보이지만 이를 생략한다면 두 테이블 간의 양방향 관계를 두 객체가 반영하지 못합니다. 

> JPA를 쓰는 근본적인 이유는 객체지향적인 코드로 관계형 DB를 다루기 위함입니다. 객체지향적인 시각에서라도 당연히 양방향 관계를 설정해주는 것이 맞습니다. - 백기선


그닥 좋아보이지 않는 방법이지만, findByIdWithAllMembers()를 호출하기 전에 강제로 em.clear()를 호출하는 방법이 있습니다. 영속성 컨텍스트를 강제로 비워 DB에서 가져온 연관된 Team 엔티티 정보를 새로 영속화시킬 수 있습니다. 스프링 프레임워크를 사용한다면 스프링에서 관리하는 EntityManager를 주입받아야 합니다.

## References

[https://www.baeldung.com/jpa-hibernate-persistence-context](https://www.baeldung.com/jpa-hibernate-persistence-context)

[https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)

[https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Propagation.html](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Propagation.html)

[https://stackoverflow.com/questions/71701031/fetch-join-with-transactional-doesnt-load-their-relational-entities](https://stackoverflow.com/questions/71701031/fetch-join-with-transactional-doesnt-load-their-relational-entities)

김영한, 자바 ORM 표준 JPA 프로그래밍
