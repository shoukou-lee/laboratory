package iam.shoukou.jpaexample.service;

import iam.shoukou.jpaexample.model.Member;
import iam.shoukou.jpaexample.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member findMemberWithOptLock(Long id) {
        return memberRepository.findByIdWithOptLock(id)
                .orElseThrow(() -> new RuntimeException("RTE"));
    }

    @Transactional
    public Member findMemberWithOptLockForceInc(Long id) {
        return memberRepository.findByIdWithOptLockForceInc(id)
                .orElseThrow(() -> new RuntimeException("RTE"));
    }

    @Transactional
    public void increaseNumber(Long id) {

        Member m = memberRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }

    @Transactional
    public void increaseNumberWithPessLock(Long id) {

        Member m = memberRepository.findByIdWithPessLock(id)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }

    @Transactional
    public void increaseNumberWithOptLock(Long id) {

        Member m = memberRepository.findByIdWithOptLock(id)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }

    @Transactional
    public void increaseNumberWithOptLockForceInc(Long id) {

        Member m = memberRepository.findByIdWithOptLockForceInc(id)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }


    @Transactional
    public void stopWatch(int waitSecs, String message, Long id) {
        log.info("**** [{}] 메서드 시작 {}", Thread.currentThread().getName(), message);
        Member m = memberRepository.findByIdWithPessLock(id)
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
