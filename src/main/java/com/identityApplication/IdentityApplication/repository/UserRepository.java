package com.identityApplication.IdentityApplication.repository;

import com.identityApplication.IdentityApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// declare the repository and indicate the entity to work with is User
// and its id's (primary key) type is String

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    // this is a magic of Spring framework
    // when we use the existByUsername here, JPA will understand and make a query to check
    // for the existence of the inputted username without any other codes.
    Optional<User> findByUsername(String username);
}
