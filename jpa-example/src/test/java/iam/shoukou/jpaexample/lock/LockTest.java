package iam.shoukou.jpaexample.lock;

import iam.shoukou.jpaexample.model.Member;
import iam.shoukou.jpaexample.repository.MemberRepository;
import iam.shoukou.jpaexample.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class LockTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("비관적 락을 걸면 DB row에 락이 걸리고 순차적인 업데이트가 보장된다")
    void pessimLockTest() throws InterruptedException {
        // given
        memberRepository.save(new Member("member"));

        // when
        List<Thread> pool = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread th = new Thread(() -> {
                memberService.increaseNumberWithPessLock("member");
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
        Assertions.assertThat(member.getNumber()).isEqualTo(10);
    }

    @Test
    @DisplayName("낙관적 락 옵션이 없어도 엔티티에 @Version이 있다면, 낙관적 락이 적용되어 엔티티 변경 시 버전이 증가한다")
    void optLock_옵션_없이_엔티티_변경() {
        // given
        Member member = memberRepository.save(new Member("member"));

        Member ret;
        for (int i = 1; i <= 10; i++) {
            memberService.increaseNumber("member");
            ret = memberRepository.findAll().get(0);
            Assertions.assertThat(ret.getVersion()).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("낙관적 락 옵션이 명시되면 엔티티 변경 시 버전이 증가한다")
    void optLock_옵션으로_엔티티_변경() {
        // given
        Member member = memberRepository.save(new Member("member"));

        Member ret;
        for (int i = 1; i <= 10; i++) {
            memberService.increaseNumberWithOptLock("member");
            ret = memberRepository.findAll().get(0);
            Assertions.assertThat(ret.getVersion()).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("낙관적 락 옵션이 명시되어도 단순 조회로는 버전이 증가하지 않는다")
    void optLock_옵션으로_엔티티_조회() {
        // given
        Member member = memberRepository.save(new Member("member"));

        Member ret;
        for (int i = 1; i <= 10; i++) {
            memberService.findMemberWithOptLock("member");
            ret = memberRepository.findAll().get(0);
            Assertions.assertThat(ret.getVersion()).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("낙관적 락+강제 버전 증가 옵션은 단순 조회를 포함한 트랜잭션 커밋 시 버전이 올라간다")
    void optLockForceInc_옵션으로_엔티티_조회() {
        // given
        Member member = memberRepository.save(new Member("member"));

        Member ret;
        for (int i = 1; i <= 10; i++) {
            memberService.findMemberWithOptLockForceInc("member");
            ret = memberRepository.findAll().get(0);
            Assertions.assertThat(ret.getVersion()).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("낙관적 락+강제 버전 증가 옵션으로 엔티티를 조회 후 수정하면 한 트랜잭션에서 버전이 2개씩 증가한다")
    void optLockForceInc_옵션으로_엔티티_조회_후_수정() {
        // given
        Member member = memberRepository.save(new Member("member"));

        Member ret;
        for (int i = 1; i <= 10; i++) {
            memberService.increaseNumberWithOptLockForceInc("member");
            ret = memberRepository.findAll().get(0);
            Assertions.assertThat(ret.getVersion()).isEqualTo(i * 2);
        }
    }

    @Test
    @DisplayName("비관적 락은 다른 트랜잭션의 진행을 락 시점부터 완전히 차단한다.")
    void test() {
        // given
        Member member = memberRepository.save(new Member("member"));

        List<Thread> pool = new ArrayList<>();

        int wait = 5;

        pool.add(new Thread(() -> memberService.stopWatch(wait,"Tx1", "member")));
        pool.add(new Thread(() -> memberService.stopWatch(wait,"Tx2", "member")));

        pool.get(0).start();
        pool.get(1).start();

        try {
            pool.get(0).join();
            pool.get(1).join();
        } catch (InterruptedException e) {
            System.out.println("Exception");
        }
    }

    // TODO : 이거 왜않됌 ?
//    @Test
//    @DisplayName("낙관적 락 테스트 - 멀티스레드가 수정 시도를 하더라도 최초 1회의 수정만을 보장한다.")
//    void optLockTest() throws InterruptedException {
//
//        // given
//        memberRepository.save(new Member("member"));
//
//        // when
//        List<Thread> pool = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Thread th = new Thread(() -> {
//                memberService.increaseNumberWithOptLock("member");
//            });
//            pool.add(th);
//        }
//
//        for (Thread t : pool) {
//            t.start();
//        }
//
//        for (Thread t : pool) {
//            t.join();
//        }
//
//        // then
//        Member member = memberRepository.findAll().get(0);
//        assertThat(member.getNumber()).isEqualTo(1);
//        assertThat(member.getVersion()).isEqualTo(1);
//
//    }
//
//    @Test
//    @DisplayName("강제 버전증가 낙관적 락 테스트 - 단순 조회를 포함한 트랜잭션 커밋 시 버전을 증가시킨다")
//    void optLockForceIncTest() throws InterruptedException {
//
//        // given
//        memberRepository.save(new Member("member"));
//
//        // when
//        List<Thread> pool = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Thread th = new Thread(() -> {
//                memberService.increaseNumberWithOptLockForceInc("member");
//            });
//            pool.add(th);
//        }
//
//        for (Thread t : pool) {
//            t.start();
//        }
//
//        for (Thread t : pool) {
//            t.join();
//        }
//
//        // then
//        Member member = memberRepository.findAll().get(0);
//        assertThat(member.getNumber()).isEqualTo(1);
//        assertThat(member.getVersion()).isEqualTo(2);
//
//    }
}
