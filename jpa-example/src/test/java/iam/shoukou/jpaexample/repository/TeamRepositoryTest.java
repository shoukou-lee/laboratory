package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.QueryDslTestConfig;
import iam.shoukou.jpaexample.model.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@ActiveProfiles("test")
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    @DisplayName("find by name")
    void findByName() {
        // given
        Team team = new Team("team A");
        teamRepository.save(team);

        // when
        Team teamA = teamRepository.findByName("team A")
                .orElseThrow(() -> new RuntimeException());

        // then
        assertThat(teamA.getName()).isEqualTo("team A");
    }

}
