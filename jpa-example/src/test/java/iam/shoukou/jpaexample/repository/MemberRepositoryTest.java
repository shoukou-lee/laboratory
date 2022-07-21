package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.QueryDslTestConfig;
import iam.shoukou.jpaexample.model.Member;
import iam.shoukou.jpaexample.model.Team;
import org.assertj.core.api.Assertions;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("조인 없이 찾는 경우")
    void findByName_without_join() {
        // given
        Team team = new Team("myTeam");
        teamRepository.save(team);

        Member member = new Member("shoukou", team);
        memberRepository.save(member);

        em.clear();

        // when
        List<Member> members = memberRepository.findByName("shoukou");

        // then
        assertThat(members).hasSize(1);
        assertThatThrownBy(() -> members.get(0).getTeam().getName())
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @DisplayName("조인해서 찾는 경우")
    void findByName_with_join() {
        // given
        Team team = new Team("myTeam");
        teamRepository.save(team);

        Member member = new Member("shoukou", team);
        memberRepository.save(member);

        em.clear();

        // when
        List<Member> members = memberRepository.findByName("shoukou", Team.class);

        // then
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getTeam().getName()).isEqualTo("myTeam");
    }

}
