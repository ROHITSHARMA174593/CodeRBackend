package com.code.codeR.repository;

import com.code.codeR.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserId(Long userId);

    @Query("SELECT up FROM UserProgress up JOIN FETCH up.user WHERE up.user.email = :email")
    Optional<UserProgress> findByUserEmail(@Param("email") String email);
}
