package org.lostnomore.backend.notification.repository;

import org.lostnomore.backend.notification.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Query("""
       SELECT un FROM UserNotification un
       JOIN FETCH un.notification
       WHERE un.user.id = :userId
       """)
    List<UserNotification> findByUserId(Long userId);
}
