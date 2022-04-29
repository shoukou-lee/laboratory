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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    @Autowired
    PlatformTransactionManager transactionManager;

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

    @ExecutionTime
    public void saveMember(Long id) {

        TransactionStatus txStatus = transactionManager.getTransaction(null);

        System.out.println("saveMember.isNewTransaction() = " + txStatus.isNewTransaction());

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ㅠㅠ"));
        System.out.println("saveMember.isNewTransaction() = " + txStatus.isNewTransaction());
        for (int i = 0; i < 10; i++) {
            Member member = new Member("name" + String.valueOf(i), team);
            memberRepository.save(member);
            team.getMembers().add(member);
        }

        System.out.println("saveMember.isNewTransaction() = " + txStatus.isNewTransaction());

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
}
