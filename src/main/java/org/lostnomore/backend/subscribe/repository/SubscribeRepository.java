package org.lostnomore.backend.subscribe.repository;

import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Subscribe s WHERE s.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
