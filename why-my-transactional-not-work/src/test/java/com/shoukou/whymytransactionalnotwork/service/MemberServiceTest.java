package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.Member;
import com.shoukou.whymytransactionalnotwork.model.Team;
import com.shoukou.whymytransactionalnotwork.repository.MemberRepository;
import com.shoukou.whymytransactionalnotwork.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void init() {

    }

    @BeforeEach
    void tearDown() {
        teamRepository.deleteAll();
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

    @Test
    void memServiceTest() {

        System.out.println("=== ???????????? ?????? === [memServiceTest-??????] isActualTransactionActive() = " + TransactionSynchronizationManager.isActualTransactionActive());

        Team t = new Team();

        teamRepository.save(t);
        System.out.println("=== ???????????? ?????? === [memServiceTest-save ??????] isActualTransactionActive() = " + TransactionSynchronizationManager.isActualTransactionActive());

        memberService.saveMember(t.getId());
        System.out.println("=== ???????????? ?????? === [memServiceTest-saveMember ??????] isActualTransactionActive() = " + TransactionSynchronizationManager.isActualTransactionActive());
    }



}
