## MemberServiceTest.memServiceTest()

A-B-C, A-B'-C, A-B''-C 등 로직에서, A&C를 분리하기 위한 Aspect를 정의하고, 개별적인 트랜잭션 단위로 설정하고 싶다 .. (즉, 로직과 Aspect가 별개의 트랜잭션을 갖도록) 

그래서 몇가지 케이스에 대해 로깅을 찍어봄 .. 

### Assumption
- MemberService.saveMember()는 특정 팀에 임의의 멤버를 N개 생성한다
- 메서드의 실행시간을 측정해 print하는 Aspect를 정의하고, saveMember() 메서드에 적용한다.
- MemberServiceTest.memServiceTest() 메서드는 Team을 저장하고, saveMember()를 호출하는 테스트 코드이다.
- memServiceTest()에서 실행되는 메서드에 @Transactional을 설정/해제하면서 트랜잭션 바운더리가 어떻게 설정되는지 확인해본다.

### @Transactional 키워드의 위치
#### Only on the memServiceTest() method
- 테스트 코드의 시작-끝 내내 
- TransactionSynchronizationManager.isActualTransactionActive() = true
- TransactionStatus.isNewTransaction() = false
- i.e., 테스트 코드 전체가 트랜잭션 바운더리로 설정됨


#### Only on the saveMember() method
- Test 메서드의 시점에서 
  - TransactionSynchronizationManager.isActualTransactionActive() = false
  - i.e., 개별 메서드 (save(), saveMember()) 단위로 트랜잭션 바운더리가 설정됨을 의미
    
- saveMember 메서드 시점에서, 
  - Aspect의 시작과 saveMember 호출, Aspect의 끝 내내 
  - TransactionSynchronizationManager.isActualTransactionActive() = true
  - TransactionStatus.isNewTransaction() = true
  - i.e., 트랜잭셔널 메서드의 Aspect에도 같은 트랜잭션 바운더리로 설정됨

#### Nowhere
- @Transactional 애노테이션이 없어서 두가지 문제가 발생
  - TransactionStatus.isNewTransaction() 호출 시 문제 -> DummyTxStatus를 가짜로 구현한다.
  - Team.getMembers().add()의 Lazy init -> members들이 준영속화 됐을것.. 주석으로 잠시 빼둔다.
- 테스트 코드의 시작-끝 내내
  - TransactionSynchronizationManager.isActualTransactionActive() = false
  - 단, SimpleJpaRepository 내부에서 로깅 코드를 심는다면 .. ? 트랜잭션이 관찰됐을 것.. 

#### Only on the Aspect
- 모든 범위에서 TransactionSynchronizationManager.isActualTransactionActive() = false
- Nowhere 케이스와의 차이는 모르겠지만, 적어도 트랜잭션이 saveMember()로 전파되지는 않는 듯 보임 ..