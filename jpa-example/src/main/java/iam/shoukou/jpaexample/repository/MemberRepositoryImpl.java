package iam.shoukou.jpaexample.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import iam.shoukou.jpaexample.model.Member;
import iam.shoukou.jpaexample.model.QMember;
import iam.shoukou.jpaexample.model.Team;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static iam.shoukou.jpaexample.model.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> findByName(String name, Class... clazz) {

        JPAQuery<Member> query = jpaQueryFactory.selectFrom(member)
                .where(member.name.eq(name));

        dynamicJoin(query, clazz);

        return query.fetch();
    }

    /**
     * 임의의 객체에 대해서 런타임에 연관관계 객체들의 타입 모음들을 판별하고,
     * parameterized Class[]를 넘겨받아 dynamic fetch join 쿼리를 만들 수는 없을까?
     */
    private void dynamicJoin(JPAQuery<Member> query, Class... clazz) {

        for (Class c : clazz) {
            if (c == Team.class) {
                query.join(member.team)
                        .fetchJoin();
            }

        }
    }

}
