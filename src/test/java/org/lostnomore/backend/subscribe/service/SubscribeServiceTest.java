package org.lostnomore.backend.subscribe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.common.ServiceTest;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.lostnomore.backend.item.elastic.LostItemSearchService;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.dto.response.RecentItemsDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribeListDto;
import org.lostnomore.backend.subscribe.manager.SubscribeRetriever;
import org.lostnomore.backend.user.domain.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest extends ServiceTest {

    @Mock
    private SubscribeRetriever subscribeRetriever;

    @Mock
    private LostItemRetriever lostItemRetriever;

    @Mock
    private LostItemSearchService lostItemSearchService;

    @InjectMocks
    private SubscribeService subscribeService;

    private Long testUserId;
    private User testUser;
    private Category category;
    private Location location;
    private Subscribe subscribe;
    private List<Subscribe> subscribes;
    private LostItemDocument lostItemDocument;
    private SearchHit<LostItemDocument> searchHit;
    private SearchHits<LostItemDocument> searchHits;
    private LostItem lostItem;
    private List<LostItem> lostItems;
    private final LocalDate dateEnd = LocalDate.now().minusDays(1);
    private final LocalDate dateStart = dateEnd.minusDays(7);

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        testUser = User.builder().name("테스트 유저").build();
        category = Category.builder().name("전자기기").build();
        location = Location.builder().name("서울").build();

        subscribe = Subscribe.builder()
                .user(testUser)
                .keyword("지갑")
                .region("서울")
                .category(category)
                .build();
        subscribes = List.of(subscribe);

        lostItemDocument = LostItemDocument.builder()
                .id(100L)
                .name("지갑")
                .region("서울")
                .build();

        searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(lostItemDocument);

        searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));

        lostItem = LostItem.builder()
                .name("지갑")
                .category(category)
                .location(location)
                .build();
        lostItems = List.of(lostItem);
    }

    @Test
    @DisplayName("구독한 키워드에 해당하는 분실물 목록을 반환한다.")
    void getRecentItemsTest() {
        // Given
        when(subscribeRetriever.findByUserId(testUserId)).thenReturn(subscribes);
        when(lostItemSearchService.searchLostItemsForSubscription(any(), any(), any(), any(), any(), any()))
                .thenReturn(searchHits);
        when(lostItemRetriever.findByIdIn(anyList())).thenReturn(lostItems);

        // When
        RecentItemsDto result = subscribeService.getRecentItems(testUserId);

        // Then
        assertThat(result.recentItems()).hasSize(1);
        assertThat(result.recentItems().get(0).name()).isEqualTo(lostItem.getName());
        assertThat(result.recentItems().get(0).location()).isEqualTo(lostItem.getLocation().getName());

        verify(subscribeRetriever, times(1)).findByUserId(testUserId);
        verify(lostItemSearchService, times(1)).searchLostItemsForSubscription(any(), any(), any(), any(), any(), any());
        verify(lostItemRetriever, times(1)).findByIdIn(anyList());
    }

    @Test
    @DisplayName("구독한 키워드의 분실물 목록을 정상적으로 반환한다.")
    void getSubscribeListTest() {
        // Given
        when(subscribeRetriever.findByUserId(testUserId)).thenReturn(subscribes);
        when(lostItemSearchService.searchLostItemsForSubscription(any(), any(), any(), any(), any(), any()))
                .thenReturn(searchHits);
        when(lostItemRetriever.findByIdInWithCursorPagination(anyList(), any(), any(), anyInt()))
                .thenReturn(lostItems);

        // When
        SubscribeListDto result = subscribeService.getSubscribeList(testUserId, dateStart, dateEnd, null, null, null, 8);

        // Then
        assertThat(result.lostItems()).hasSize(1);
        assertThat(result.lostItems().get(0).name()).isEqualTo("지갑");
        assertThat(result.lostItems().get(0).location()).isEqualTo("서울");

        verify(subscribeRetriever, times(1)).findByUserId(testUserId);
        verify(lostItemSearchService, times(1)).searchLostItemsForSubscription(any(), any(), any(), any(), any(), any());
        verify(lostItemRetriever, times(1)).findByIdInWithCursorPagination(anyList(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("검색된 분실물이 없으면 빈 목록을 반환한다.")
    void getSubscribeList_noResult() {
        // Given
        when(subscribeRetriever.findByUserId(testUserId)).thenReturn(subscribes);
        when(lostItemSearchService.searchLostItemsForSubscription(any(), any(), any(), any(), any(), any()))
                .thenReturn(searchHits);
        when(lostItemRetriever.findByIdInWithCursorPagination(anyList(), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        // When
        SubscribeListDto result = subscribeService.getSubscribeList(testUserId, dateStart, dateEnd, null, null, null, 5);

        // Then
        assertThat(result.lostItems()).isEmpty();
        assertThat(result.nextCursorDate()).isNull();
        assertThat(result.nextCursorId()).isNull();
    }
}
