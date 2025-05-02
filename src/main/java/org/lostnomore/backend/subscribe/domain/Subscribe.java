package org.lostnomore.backend.subscribe.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lostnomore.backend.global.domain.BaseEntity;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.user.domain.User;

@Getter
@Table(name = "subscribe",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_keyword", columnNames = {"user_id", "category_id", "keyword", "region"})
        })
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscribe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(
            name = "fk_subscribe_user",
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
    ))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "region")
    private String region;

    @Builder
    public Subscribe(User user, Category category, String keyword, String region) {
        this.user = user;
        this.category = category;
        this.keyword = keyword;
        this.region = region;
    }

    public void updateSubscribe(
            String keyword,
            Category category,
            String region
    ) {
        this.keyword = keyword;
        this.category = category;
        this.region = region;
    }
}