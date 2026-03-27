package com.mykidevs.userservice.repositories;


import com.mykidevs.userservice.models.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyTokenRepository extends JpaRepository<VerifyToken, Long> {

    @Query("SELECT vt.user.id FROM VerifyToken vt WHERE vt.value = :token")
    Optional<Long> findUserIdByToken(@Param("token") String token);

    void deleteByValueAndUserId(String token, Long user_id);

    void deleteByUserId(Long userId);
}
