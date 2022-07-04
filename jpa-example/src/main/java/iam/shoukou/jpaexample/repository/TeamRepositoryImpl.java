package iam.shoukou.jpaexample.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import iam.shoukou.jpaexample.model.QTeam;
import iam.shoukou.jpaexample.model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import static iam.shoukou.jpaexample.model.QTeam.team;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Team> findByName(String name) {
        List<Team> teams = jpaQueryFactory.selectFrom(team)
                .where(team.name.eq(name))
                .fetch();

        if (teams.size() != 1) {
            throw new RuntimeException();
        }

        return Optional.ofNullable(teams.get(0));
    }
}
