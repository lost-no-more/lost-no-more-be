package org.lostnomore.backend.notification.repository;

import org.lostnomore.backend.notification.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Query("""
       SELECT un FROM UserNotification un
       JOIN FETCH un.notification n
       JOIN FETCH n.category
       WHERE un.user.id = :userId
       """)
    List<UserNotification> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Subscribe s WHERE s.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
