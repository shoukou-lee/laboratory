package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.Team;
import com.shoukou.whymytransactionalnotwork.model.Member;
import com.shoukou.whymytransactionalnotwork.repository.TeamRepository;
import com.shoukou.whymytransactionalnotwork.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void init() {

    }

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Transactional
    @Test
    void transactionalFetchJoin() {
        System.out.println("save team");
        Team team = new Team();
        Team saved = teamRepository.save(team);

        System.out.println("save members");
        for (int i = 0; i < 10; i++) {
            Member member = new Member("name" + String.valueOf(i), team);
            memberRepository.save(member);
        }

        /**
         * 1 query was observed -
         * 1) select member_id, name, team_id from member (but only member_id was extracted)
         */
        System.out.println("memberRepository.findAll()");
        List<Member> members = memberRepository.findAll();

        System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
        Team t = teamRepository.findByIdWithAllMembers(saved.getId())
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

        assertThat(t.getMembers().size()).isEqualTo(0);
    }

    @Test
    void nonTransactionalFetchJoin() {
        System.out.println("save team");
        Team team = new Team();
        Team saved = teamRepository.save(team);

        System.out.println("save members");
        for (int i = 0; i < 10; i++) {
            Member member = new Member("name" + String.valueOf(i), team);
            memberRepository.save(member);
        }

        /**
         * 2 queries were observed -
         * 1) select member_id, name, team_id from member (all of these were extracted)
         * 2) select team_id from team where team_id = :team_id
         */
        System.out.println("memberRepository.findAll()");
        List<Member> members = memberRepository.findAll();

        System.out.println("teamRepository.findByIdWithAllMembers(saved.getId())");
        Team t = teamRepository.findByIdWithAllMembers(saved.getId())
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

        assertThat(t.getMembers().size()).isEqualTo(10);
    }

    @Test
    void callSaveAndThrowRunTimeException() {
        // when
        assertThatThrownBy(() -> memberService.saveAndThrowRunTimeException())
                .isInstanceOf(RuntimeException.class);

        // then
        List<Member> members = memberRepository.findAll();
        assertThat(members.isEmpty()).isEqualTo(false); // expected : non-rollback
    }

    @Test
    void callTxSaveAndThrowRunTimeException() {
        // when
        assertThatThrownBy(() -> memberService.txSaveAndThrowRunTimeException())
                .isInstanceOf(RuntimeException.class);

        // then
        List<Member> members = memberRepository.findAll();
        assertThat(members.isEmpty()).isEqualTo(true); // expected : rollback
    }


    @Test
    void callMethod() {
        // when
        assertThatThrownBy(() -> memberService.callMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<Member> members = memberRepository.findAll();
        assertThat(members.isEmpty()).isEqualTo(false); // expected : non-rollback
    }

    @Test
    void callTxMethod() {
        // when
        assertThatThrownBy(() -> memberService.callTxMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<Member> members = memberRepository.findAll();
        assertThat(members.isEmpty()).isEqualTo(false); // expected : non-rollback
    }

    @Test
    void txCallMethod() {
        // when
        assertThatThrownBy(() -> memberService.txCallMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<Member> members = memberRepository.findAll();
        assertThat(members.isEmpty()).isEqualTo(true); // expected : rollback
    }

    @Test
    void txCallTxMethod() {
        // when
        assertThatThrownBy(() -> memberService.txCallTxMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<Member> members = memberRepository.findAll();
        assertThat(members.isEmpty()).isEqualTo(true); // expected : rollback
    }


}
