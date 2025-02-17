package org.lostnomore.backend.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lostnomore.backend.global.domain.BaseEntity;
import org.lostnomore.backend.user.domain.User;

@Getter
@Table(name = "user_notification")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(name = "read_status", nullable = false)
    private Boolean readStatus;

    @Builder
    public UserNotification(User user, Notification notification, Boolean readStatus) {
        this.user = user;
        this.notification = notification;
        this.readStatus = readStatus;
    }

    public void updateReadStatus() {
        this.readStatus = true;
    }
}
