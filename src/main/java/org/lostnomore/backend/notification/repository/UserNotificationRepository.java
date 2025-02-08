package org.lostnomore.backend.notification.repository;

import org.lostnomore.backend.notification.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

}
