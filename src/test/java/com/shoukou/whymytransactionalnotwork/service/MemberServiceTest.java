package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.Team;
import com.shoukou.whymytransactionalnotwork.model.Member;
import com.shoukou.whymytransactionalnotwork.repository.TeamRepository;
import com.shoukou.whymytransactionalnotwork.repository.MemberRepository;
import org.assertj.core.api.AbstractIntegerAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("h2-test")
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
