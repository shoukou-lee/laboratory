package com.shoukou.whymytransactionalnotwork.repository;

import com.shoukou.whymytransactionalnotwork.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query(value =
            "select distinct t from Team t " +
            "join fetch t.members " +
            "where t.id = :id")
    Optional<Team> findByIdWithAllMembers(Long id);
}
