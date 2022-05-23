package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.aop.ExecutionTime;
import com.shoukou.whymytransactionalnotwork.model.Member;
import com.shoukou.whymytransactionalnotwork.model.Team;
import com.shoukou.whymytransactionalnotwork.repository.MemberRepository;
import com.shoukou.whymytransactionalnotwork.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public void callMethod() {
        saveAndThrowRunTimeException();
    }

    public void callTxMethod() {
        txSaveAndThrowRunTimeException();
    }

    @Transactional
    public void txCallMethod() {
        saveAndThrowRunTimeException();
    }

    @Transactional
    public void txCallTxMethod() {
        txSaveAndThrowRunTimeException();
    }

    public void saveAndThrowRunTimeException() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Member u = new Member("User" + String.valueOf(i));
            members.add(u);
        }
        memberRepository.saveAll(members);

        throw new RuntimeException("throw runtime exception");
    }

    @Transactional
    public void txSaveAndThrowRunTimeException() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Member u = new Member("User" + String.valueOf(i));
            members.add(u);
        }
        memberRepository.saveAll(members);

        throw new RuntimeException("throw runtime exception");
    }

    @ExecutionTime
    @Transactional
    public Long saveTeam() {
        Team team = new Team("teamA");
        Team saved = teamRepository.save(team);


        return saved.getId();
    }

    @Transactional
    @ExecutionTime
    public void saveMember(Long teamId) {

        TransactionStatus txStatus = TransactionAspectSupport.currentTransactionStatus();
        // TransactionStatus txStatus = new DummyTxStatus();

        log.info("\n======\n 트랜잭션 로깅 \n isActualTransactionActive() : {}\nisNewTransaction() : {}\n=====", TransactionSynchronizationManager.isActualTransactionActive(), txStatus.isNewTransaction());

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

        log.info("\n======\n 트랜잭션 로깅 \n isActualTransactionActive() : {}\nisNewTransaction() : {}\n=====", TransactionSynchronizationManager.isActualTransactionActive(), txStatus.isNewTransaction());
        for (int i = 0; i < 3; i++) {
            Member member = new Member("name" + String.valueOf(i), team);
            memberRepository.save(member);
            team.getMembers().add(member);
            log.info("\n======\n 트랜잭션 로깅 \n isActualTransactionActive() : {}\nisNewTransaction() : {}\n=====", TransactionSynchronizationManager.isActualTransactionActive(), txStatus.isNewTransaction());
        }

        log.info("\n======\n 트랜잭션 로깅 \n isActualTransactionActive() : {}\nisNewTransaction() : {}\n=====", TransactionSynchronizationManager.isActualTransactionActive(), txStatus.isNewTransaction());

        System.out.println("save member done");
    }

    @ExecutionTime
    @Transactional(readOnly = true)
    public void fetchJoin(Long id) {
        Team t = teamRepository.findByIdWithAllMembers(id)
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));

        for (Member member : t.getMembers()) {
            System.out.println("member.getName() = " + member.getName());
        }
    }

    @Transactional
    public void increaseNumber(String name) {

        Member m = memberRepository.findMemberByName(name)
                .orElseThrow(() -> new RuntimeException("RTE"));

        m.setNumber(m.getNumber() + 1);
        log.info(":::::: 현재 번호 = {}", m.getNumber());
    }
}
