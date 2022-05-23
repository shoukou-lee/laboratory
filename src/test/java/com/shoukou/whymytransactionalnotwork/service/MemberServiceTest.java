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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
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

    @Autowired
    PlatformTransactionManager transactionManager;

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

    @Test
    void memServiceTest() {

        System.out.println("=== 트랜잭션 로깅 === [memServiceTest-시작] isActualTransactionActive() = " + TransactionSynchronizationManager.isActualTransactionActive());

        Team t = new Team();

        teamRepository.save(t);
        System.out.println("=== 트랜잭션 로깅 === [memServiceTest-save 이후] isActualTransactionActive() = " + TransactionSynchronizationManager.isActualTransactionActive());

        memberService.saveMember(t.getId());
        System.out.println("=== 트랜잭션 로깅 === [memServiceTest-saveMember 이후] isActualTransactionActive() = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Test
    @DisplayName("비관적 락 테스트")
    void pessimLockTest() throws InterruptedException {
        // given
        memberRepository.save(new Member("member"));

        // when
        List<Thread> pool = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread th = new Thread(() -> {
                memberService.increaseNumber("member");
            });
            pool.add(th);
        }

        for (Thread t : pool) {
            t.start();
        }

        for (Thread t : pool) {
            t.join();
        }

        // then
        Member member = memberRepository.findAll().get(0);
        assertThat(member.getNumber()).isEqualTo(10);
    }

}
