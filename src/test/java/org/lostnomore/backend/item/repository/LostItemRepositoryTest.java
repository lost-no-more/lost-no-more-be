
package org.lostnomore.backend.item.repository;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.common.RepositoryTest;
import org.lostnomore.backend.item.domain.LostItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class LostItemRepositoryTest extends RepositoryTest {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Test
    @DisplayName("오늘 등록된 분실물 개수와 총 개수를 올바르게 조회한다.")
    void findItemCountByCreatedAt() {
        // Given
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

        LostItem item1 = LostItem.builder()
                .name("지갑")
                .date(LocalDate.now().minusDays(1))
                .build();
        lostItemRepository.save(item1);

        LostItem item2 = LostItem.builder()
                .name("가방")
                .date(LocalDate.now())
                .build();
        lostItemRepository.save(item2);

        LostItem item3 = LostItem.builder()
                .name("핸드폰")
                .date(LocalDate.now())
                .build();
        lostItemRepository.save(item3);

        // When
        Tuple result = lostItemRepository.findItemCountByCreatedAtAfter(todayStart);

        // Then
        assertThat(result.get(0, Long.class)).isEqualTo(3);
        assertThat(result.get(1, Long.class)).isEqualTo(3);
    }


    @Test
    @DisplayName("등록된 분실물이 없으면 0을 반환한다.")
    void findItemCountByCreatedAt_Empty() {
        // Given
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

        // When
        Tuple result = lostItemRepository.findItemCountByCreatedAtAfter(todayStart);

        // Then
        assertThat(result.get(0, Long.class)).isEqualTo(0);
        assertThat(result.get(1, Long.class)).isEqualTo(0);
    }

}
