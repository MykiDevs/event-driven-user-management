package com.mykidevs.userservice.repositories;


import com.mykidevs.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByHasVerifiedEmail(boolean hasVerifiedEmail);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.hasVerifiedEmail = true WHERE u.id = :userId")
    void activateUser(@Param("userId") Long userId);

    Optional<User> getByEmail(String email);

    boolean existsByEmail(String email);
}
