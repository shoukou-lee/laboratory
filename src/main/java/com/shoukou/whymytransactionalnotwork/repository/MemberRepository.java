package com.shoukou.whymytransactionalnotwork.repository;

import com.shoukou.whymytransactionalnotwork.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.name = :name")
    Optional<Member> findMemberByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.name = :name")
    Optional<Member> findMemberByNameWithPessLock(String name);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select m from Member m where m.name = :name")
    Optional<Member> findMemberByNameWithOptLock(String name);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select m from Member m where m.name = :name")
    Optional<Member> findMemberByNameWithOptLockForceInc(String name);

}
