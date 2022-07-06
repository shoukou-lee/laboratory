package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {

}
