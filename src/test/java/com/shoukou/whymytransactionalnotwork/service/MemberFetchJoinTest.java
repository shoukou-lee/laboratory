package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.Member;
import com.shoukou.whymytransactionalnotwork.model.Team;
import com.shoukou.whymytransactionalnotwork.repository.MemberRepository;
import com.shoukou.whymytransactionalnotwork.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Team/Member가 1:N 연관인 경우 Team을 조회할 때 List<Member> members의 정보가 같이 로드되는지를 테스트합니다.
 * https://stackoverflow.com/questions/71701031/fetch-join-with-transactional-doesnt-load-their-relational-entities
 */

@ActiveProfiles("h2-test")
@SpringBootTest
public class MemberFetchJoinTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EntityManager em;

    /**
     * SimpleJpaRepository.save()는 @Transactional이 적용되어 있다.
     * @Transactional 의 default propagation policy는 REQUIRED로,
     * 부모 트랜잭션이 있다면 참여하고, 없다면 새 트랜잭션을 시작한다.
     */
    @Transactional
    @Test
    void transactionalFetchJoin() {
        // Team, Member가 영속 컨텍스트에 저장
        // SimpleJpaRepository.save()의 트랜잭션은 이 메서드의 트랜잭션에 참여하므로, 같은 영속 컨텍스트 사용
        Team team = new Team("teamA");
        Team saved = teamRepository.save(team);

        for (int i = 0; i < 10; i++) {
            Member member = new Member("name" + String.valueOf(i), team);
            memberRepository.save(member);
        }

        // DB로 JPQL을 쿼리, Team과 Member를 함께 가져와 영속 컨텍스트에 저장 시도
        // 이미 영속 컨텍스트에 Team, Member가 있기 때문에 DB에서 조회한 결과는 버려짐
        System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
        Team t = teamRepository.findByIdWithAllMembers(saved.getId())
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

        assertThat(t.getMembers().size()).isEqualTo(0);

        team.setName("teamB");
        assertThat(
                teamRepository.findByName("teamB")
                        .orElseThrow(() -> new RuntimeException("exception이면, dirty check이 안된 것이고 영속 상태가 아님을 의미함."))
                        .getName()
        ).isEqualTo("teamB");
    }

    @Test
    void nonTransactionalFetchJoin() {
        // Team, Member가 영속 컨텍스트에 저장 후 SimpleJpaRepository.save()가 끝나면서 트랜잭션 commit
        Team team = new Team("teamA");
        Team saved = teamRepository.save(team); // 영속 컨텍스트 종료, team detached


        for (int i = 0; i < 10; i++) {
            Member member = new Member("name" + String.valueOf(i), team);
            memberRepository.save(member); // 영속 컨텍스트 종료, member detached
        }

        // DB로 JPQL을 쿼리, Team과 Member를 함께 가져와 영속 컨텍스트에 저장 시도
        // 영속 컨텍스트에 Team, Member가 없기 때문에 DB에서 조회한 결과가 영속 컨텍스트에 저장됨
        System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
        Team t = teamRepository.findByIdWithAllMembers(saved.getId())
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

        assertThat(t.getMembers().size()).isEqualTo(10);

        team.setName("teamB");
        assertThatThrownBy(
                () -> teamRepository.findByName("teamB")
                        .orElseThrow(() -> new RuntimeException("exception이면, dirty check이 안된 것이고 영속 상태가 아님을 의미함."))
        ).isInstanceOf(RuntimeException.class);
    }

}
