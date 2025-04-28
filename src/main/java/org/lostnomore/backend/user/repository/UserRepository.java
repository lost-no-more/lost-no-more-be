package org.lostnomore.backend.user.repository;

import java.util.Optional;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderId(Long providerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Subscribe s WHERE s.user.id = :userId")
    void deleteByUserId(Long userId);
}
