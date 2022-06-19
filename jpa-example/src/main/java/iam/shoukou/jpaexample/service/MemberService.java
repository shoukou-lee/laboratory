package iam.shoukou.jpaexample.service;

import iam.shoukou.jpaexample.model.Member;
import iam.shoukou.jpaexample.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member findMemberWithOptLock(String name) {
        return memberRepository.findMemberByNameWithOptLock(name)
                .orElseThrow(() -> new RuntimeException("RTE"));
    }

    @Transactional
    public Member findMemberWithOptLockForceInc(String name) {
        return memberRepository.findMemberByNameWithOptLockForceInc(name)
                .orElseThrow(() -> new RuntimeException("RTE"));
    }

    @Transactional
    public void increaseNumber(String name) {

        Member m = memberRepository.findMemberByName(name)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }

    @Transactional
    public void increaseNumberWithPessLock(String name) {

        Member m = memberRepository.findMemberByNameWithPessLock(name)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }

    @Transactional
    public void increaseNumberWithOptLock(String name) {

        Member m = memberRepository.findMemberByNameWithOptLock(name)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }

    @Transactional
    public void increaseNumberWithOptLockForceInc(String name) {

        Member m = memberRepository.findMemberByNameWithOptLockForceInc(name)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }


    @Transactional
    public void stopWatch(int waitSecs, String message, String name) {
        log.info("**** [{}] 메서드 시작 {}", Thread.currentThread().getName(), message);
        Member m = memberRepository.findMemberByNameWithPessLock(name)
                .orElseThrow(() -> new RuntimeException("RTE"));

        for (int i = 0; i < waitSecs; i++) {
            log.info("**** [{}] {}초 ({})", Thread.currentThread().getName(), i, message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Exception");
            }
        }
        m.setName(message);
        log.info("**** [{}] m.getName() = {}}", Thread.currentThread().getName(), m.getName());
        System.out.println("**** m.getName() = " + m.getName());
    }
}
