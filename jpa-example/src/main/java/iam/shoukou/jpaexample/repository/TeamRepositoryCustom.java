package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.Team;

import java.util.Optional;

public interface TeamRepositoryCustom {

    Optional<Team> findByName(String name);

}
