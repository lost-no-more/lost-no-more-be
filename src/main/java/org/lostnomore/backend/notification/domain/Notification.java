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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lostnomore.backend.global.domain.BaseEntity;
import org.lostnomore.backend.item.domain.Category;

@Getter
@Table(name = "notification")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name="region", nullable = false)
    private String region;

    @Column(name = "ids", nullable = false)
    private String ids;

    @Builder
    public Notification(Category category, int totalCount, LocalDate date, String keyword, String region, String ids) {
        this.category = category;
        this.totalCount = totalCount;
        this.date = date;
        this.keyword = keyword;
        this.region = region;
        this.ids = ids;
    }
}
