package iam.shoukou.jpaexample.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import iam.shoukou.jpaexample.model.QTeam;
import iam.shoukou.jpaexample.model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.Querydsl;

import java.util.List;
import java.util.Optional;

import static iam.shoukou.jpaexample.model.QTeam.team;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Team> findByName(String name) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(team)
                        .where(team.name.eq(name))
                        .fetchOne());
    }

    @Override
    public Optional<Team> findByIdWithAllMembers(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(team)
                        .join(team.members)
                        .fetchJoin()
                        .where(team.id.eq(id))
                        .fetchOne());
    }

    /**
    select
    team0_.team_id as team_id1_2_,
    team0_.name as name2_2_
            from
    team team0_
    order by
    team0_.name asc limit ? */
    @Override
    public List<Team> findWithPagination(int pageNumber, int pageSize) {
        return jpaQueryFactory.selectFrom(team)
                .orderBy(team.name.asc())
                .limit(pageSize)
                .offset(pageNumber * pageSize)
                .fetch();
    }

    @Override
    public List<Team> findWithNoOffsetPagination(Long teamId, int pageSize) {
        return jpaQueryFactory.selectFrom(team)
                .from(team)
                .where(ltId(teamId))
                .orderBy(team.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltId(Long teamId) {
        if (teamId == null) {
            return null;
        }

        return team.id.lt(teamId);
    }

}
