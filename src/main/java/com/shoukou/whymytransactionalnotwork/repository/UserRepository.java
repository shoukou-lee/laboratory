package com.shoukou.whymytransactionalnotwork.repository;

import com.shoukou.whymytransactionalnotwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
