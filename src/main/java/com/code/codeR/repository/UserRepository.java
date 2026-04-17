package com.code.codeR.repository;

import com.code.codeR.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"solvedProblems"})
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithSolvedProblems(@org.springframework.data.repository.query.Param("email") String email);
    
    boolean existsByEmail(String email);
}
