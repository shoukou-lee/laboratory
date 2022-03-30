package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.Member;
import com.shoukou.whymytransactionalnotwork.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

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

}
