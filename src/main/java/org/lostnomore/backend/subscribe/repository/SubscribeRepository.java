package org.lostnomore.backend.subscribe.repository;

import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Query("""
       SELECT s FROM Subscribe s
       JOIN FETCH s.category
       WHERE s.user.id = :userId
       """)
    List<Subscribe> findByUserId(Long userId);
}
