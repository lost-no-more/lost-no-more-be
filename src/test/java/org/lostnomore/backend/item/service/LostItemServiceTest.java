package org.lostnomore.backend.item.service;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.item.dto.response.ItemsCountDto;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.lostnomore.backend.item.repository.LostItemRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LostItemServiceTest {

    @Mock
    private LostItemRepository lostItemRepository;

    @Mock
    private LostItemRetriever lostItemRetriever;

    @InjectMocks
    private LostItemService lostItemService;

    private Tuple mockTuple;

    @Test
    @DisplayName("오늘 등록된 분실물 개수와 총 개수를 정확히 반환해야 한다.")
    void getItemsCount() {
        // Given
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        mockTuple = mock(Tuple.class);
        when(mockTuple.get(0, Long.class)).thenReturn(5L);
        when(mockTuple.get(1, Long.class)).thenReturn(10L);
        when(lostItemRetriever.findItemCountByCreatedAtAfter(todayStart)).thenReturn(mockTuple);

        // When
        ItemsCountDto result = lostItemService.getItemsCount();

        // Then
        assertThat(result.today()).isEqualTo(5);
        assertThat(result.total()).isEqualTo(10);
    }

    @Test
    @DisplayName("등록된 분실물이 없으면 0을 반환해야 한다.")
    void getItemsCount_Empty() {
        // Given
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Tuple mockTuple = mock(Tuple.class);
        when(mockTuple.get(0, Long.class)).thenReturn(0L);
        when(mockTuple.get(1, Long.class)).thenReturn(0L);
        when(lostItemRetriever.findItemCountByCreatedAtAfter(todayStart)).thenReturn(mockTuple);

        // When
        ItemsCountDto result = lostItemService.getItemsCount();

        // Then
        assertThat(result.today()).isEqualTo(0);
        assertThat(result.total()).isEqualTo(0);
    }

}
