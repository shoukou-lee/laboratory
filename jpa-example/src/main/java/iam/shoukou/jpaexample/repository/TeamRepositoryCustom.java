package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepositoryCustom {

    Optional<Team> findByName(String name);

    Optional<Team> findByIdWithAllMembers(Long id);

    List<Team> findWithPagination(int pageNumber, int pageSize);

    List<Team> findWithNoOffsetPagination(Long teamId, int pageSize);

}
