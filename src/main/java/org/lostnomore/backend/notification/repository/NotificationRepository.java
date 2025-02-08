package org.lostnomore.backend.notification.repository;

import org.lostnomore.backend.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
