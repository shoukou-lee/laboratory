package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdWithPessLock(Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdWithOptLock(Long id);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdWithOptLockForceInc(Long id);

}
