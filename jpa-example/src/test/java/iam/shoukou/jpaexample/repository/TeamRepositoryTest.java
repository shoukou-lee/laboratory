package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.QueryDslTestConfig;
import iam.shoukou.jpaexample.model.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void init() {
        teamRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
    }

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

    @Test
    @DisplayName("pagination을 적용한 조회")
    void findWithPagination() {
        // given
        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Team team = new Team(String.format("%03d", i));
            teams.add(team);
        }
        teamRepository.saveAll(teams);

        // when
        int pageNumber = 0;
        int pageSize = 10;
        List<Team> pagedTeam = teamRepository.findWithPagination(pageNumber, pageSize);

        // then
        assertThat(pagedTeam).hasSize(pageSize);
        for (Team team : pagedTeam) {
            System.out.println("team.getName() = " + team.getName());
        }
    }

    @Test
    @DisplayName("No-offset pagination을 적용한 첫 페이지 조회")
    void findWithNoOffsetPagination_firstPage() {
        // given
        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Team team = new Team(String.format("%03d", i));
            teams.add(team);
        }
        teamRepository.saveAll(teams);

        // when
        Long lastId = teams.get(teams.size() - 1).getId();
        int pageSize = 10;
        List<Team> pagedTeam = teamRepository.findWithNoOffsetPagination(null, pageSize);

        // then
        assertThat(pagedTeam).hasSize(pageSize);
        assertThat(pagedTeam.get(0).getId()).isEqualTo(lastId);
        assertThat(pagedTeam.get(pagedTeam.size() - 1).getId()).isEqualTo(lastId - pageSize + 1);
    }

    @Test
    @DisplayName("No-offset pagination을 적용한 두번째 페이지 조회")
    void findWithNoOffsetPagination_secondPage() {
        // given
        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Team team = new Team(String.format("%03d", i));
            teams.add(team);
        }
        teamRepository.saveAll(teams);

        // when
        Long lastId = teams.get(teams.size() - 1).getId();
        int pageSize = 10;
        List<Team> pagedTeam = teamRepository.findWithNoOffsetPagination(lastId - pageSize + 1, pageSize);

        // then
        assertThat(pagedTeam).hasSize(pageSize);
        assertThat(pagedTeam.get(0).getId()).isEqualTo(lastId - pageSize);
        assertThat(pagedTeam.get(pagedTeam.size() - 1).getId()).isEqualTo(lastId - 2 * pageSize + 1);
    }

}
