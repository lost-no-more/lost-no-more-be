package org.lostnomore.backend.subscribe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.common.ServiceTest;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.SubscribeErrorCode;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.lostnomore.backend.item.elastic.LostItemSearchService;
import org.lostnomore.backend.item.manager.CategoryRetriever;
import org.lostnomore.backend.item.manager.LocationRetriever;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.dto.request.SubscribeCreateDto;
import org.lostnomore.backend.subscribe.dto.response.RecentItemsDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribeListDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribesDto;
import org.lostnomore.backend.subscribe.manager.SubscribeCreator;
import org.lostnomore.backend.subscribe.manager.SubscribeEditor;
import org.lostnomore.backend.subscribe.manager.SubscribeRemover;
import org.lostnomore.backend.subscribe.manager.SubscribeRetriever;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserRetriever;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest extends ServiceTest {

    @Mock
    private UserRetriever userRetriever;

    @Mock
    private CategoryRetriever categoryRetriever;

    @Mock
    private LocationRetriever locationRetriever;

    @Mock
    private SubscribeCreator subscribeCreator;

    @Mock
    private SubscribeRetriever subscribeRetriever;

    @Mock
    private SubscribeEditor subscribeEditor;

    @Mock
    private SubscribeRemover subscribeRemover;

    @Mock
    private LostItemRetriever lostItemRetriever;

    @Mock
    private LostItemSearchService lostItemSearchService;

    @InjectMocks
    private SubscribeService subscribeService;

    private Long testUserId;
    private Long anotherUserId;
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
    private SubscribeCreateDto subscribeCreateDto;
    private final LocalDate dateEnd = LocalDate.now().minusDays(1);
    private final LocalDate dateStart = dateEnd.minusDays(7);

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        testUserId = 1L;

        testUser = User.builder().name("테스트 유저").build();

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(testUser, testUserId);

        category = Category.builder().name("지갑").build();
        location = Location.builder().name("서울").build();

        subscribeCreateDto = new SubscribeCreateDto("고양이지갑", "지갑", "서울");

        subscribe = Subscribe.builder()
                .user(testUser)
                .keyword("고양이지갑")
                .region("서울")
                .category(category)
                .build();
        subscribes = List.of(subscribe);

        lostItemDocument = LostItemDocument.builder()
                .id(100L)
                .name("고양이지갑")
                .region("서울")
                .build();

        searchHit = mock(SearchHit.class);
        lenient().when(searchHit.getContent()).thenReturn(lostItemDocument);

        searchHits = mock(SearchHits.class);
        lenient().when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));

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
        SubscribeListDto result = subscribeService.getSubscribeList(testUserId, dateStart, dateEnd, null, null, null, null,null,8);

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
        SubscribeListDto result = subscribeService.getSubscribeList(testUserId, dateStart, dateEnd, null, null, null, null,null,5);

        // Then
        assertThat(result.lostItems()).isEmpty();
        assertThat(result.nextCursorDate()).isNull();
        assertThat(result.nextCursorId()).isNull();
    }

    @Test
    @DisplayName("구독 생성이 성공한다.")
    void createSubscribeTest() {
        // Given
        when(userRetriever.findById(anyLong())).thenReturn(testUser);
        when(categoryRetriever.findByName(anyString())).thenReturn(category);
        when(locationRetriever.findByRegion(anyString())).thenReturn(List.of(location));

        // When
        subscribeService.createSubscribe(testUserId, subscribeCreateDto);

        // Then
        verify(userRetriever, times(1)).findById(testUserId);
        verify(categoryRetriever, times(1)).findByName(subscribeCreateDto.category());
        verify(locationRetriever, times(1)).findByRegion(subscribeCreateDto.region());
        verify(subscribeCreator, times(1)).save(argThat(subscribe ->
                subscribe.getUser().equals(testUser) &&
                        subscribe.getKeyword().equals(subscribeCreateDto.keyword()) &&
                        subscribe.getRegion().equals(subscribeCreateDto.region()) &&
                        subscribe.getCategory().equals(category)
        ));
    }

    @Test
    @DisplayName("중복 구독이 있으면 예외를 반환한다.")
    void createSubscribe_DuplicateTest() {
        // Given
        when(userRetriever.findById(anyLong())).thenReturn(testUser);
        when(categoryRetriever.findByName(anyString())).thenReturn(category);
        when(locationRetriever.findByRegion(anyString())).thenReturn(List.of(location));
        doThrow(new DataIntegrityViolationException("Duplicate entry"))
                .when(subscribeCreator).save(any(Subscribe.class));

        // When & Then
        assertThatThrownBy(() -> subscribeService.createSubscribe(testUserId, subscribeCreateDto))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(SubscribeErrorCode.SUBSCRIBE_DUPLICATE);
                    assertThat(businessException.getErrorCode().getMessage()).isEqualTo("이미 등록된 구독 정보입니다.");
                });

        verify(subscribeCreator, times(1)).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("구독 목록을 조회하면 정상적으로 반환된다.")
    void getSubscribeTest() {
        // Given
        when(subscribeRetriever.findByUserId(anyLong())).thenReturn(subscribes);

        // When
        SubscribesDto result = subscribeService.getSubscribes(testUserId);

        // Then
        assertThat(result.subscribes()).hasSize(1);
        assertThat(result.subscribes().get(0).keyword()).isEqualTo("고양이지갑");
        assertThat(result.subscribes().get(0).region()).isEqualTo("서울");

        verify(subscribeRetriever, times(1)).findByUserId(testUserId);
    }

    @Test
    @DisplayName("구독 삭제가 성공한다.")
    void deleteSubscribeTest() {
        // Given
        when(subscribeRetriever.findById(anyLong())).thenReturn(subscribe);

        // When
        subscribeService.deleteSubscribe(testUserId, 100L);

        // Then
        verify(subscribeRetriever, times(1)).findById(100L);
        verify(subscribeRemover, times(1)).deleteById(100L);
    }

    @Test
    @DisplayName("다른 유저의 구독을 삭제하면 예외가 발생한다.")
    void deleteSubscribe_ForbiddenTest() throws NoSuchFieldException, IllegalAccessException {
        // Given
        anotherUserId = 2L;
        User anotherUser = User.builder().name("다른 유저").build();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(anotherUser, anotherUserId);
        Subscribe anotherSubscribe = Subscribe.builder()
                .user(anotherUser)
                .keyword("노트북")
                .region("부산")
                .category(category)
                .build();
        when(subscribeRetriever.findById(anyLong())).thenReturn(anotherSubscribe);

        // When & Then
        assertThatThrownBy(() -> subscribeService.deleteSubscribe(testUserId, 100L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(SubscribeErrorCode.SUBSCRIBE_FORBIDDEN);
                    assertThat(businessException.getErrorCode().getMessage()).isEqualTo("해당 구독에 대한 권한이 없습니다.");
                });

        verify(subscribeRemover, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("구독 정보 업데이트가 성공한다.")
    void updateSubscribeTest() {
        // Given
        SubscribeCreateDto updatedSubscribeDto = new SubscribeCreateDto("백팩", "가방", "부산");
        when(subscribeRetriever.findById(anyLong())).thenReturn(subscribe);
        when(categoryRetriever.findByName(anyString())).thenReturn(category);
        when(locationRetriever.findByRegion(anyString())).thenReturn(List.of(location));

        // When
        subscribeService.updateSubscribe(testUserId, 100L, updatedSubscribeDto);

        // Then
        verify(subscribeEditor, times(1)).updateSubscribe(subscribe, "백팩", category, "부산");
    }

    @Test
    @DisplayName("다른 유저의 구독을 수정하면 예외가 발생한다.")
    void updateSubscribe_ForbiddenTest() throws NoSuchFieldException, IllegalAccessException {
        // Given
        anotherUserId = 2L;
        User anotherUser = User.builder().name("다른 유저").build();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(anotherUser, anotherUserId);
        Subscribe anotherSubscribe = Subscribe.builder()
                .user(anotherUser)
                .keyword("노트북")
                .region("부산")
                .category(category)
                .build();
        SubscribeCreateDto updatedSubscribeDto = new SubscribeCreateDto("가방", "부산", "전자기기");

        when(subscribeRetriever.findById(anyLong())).thenReturn(anotherSubscribe);

        // When & Then
        assertThatThrownBy(() -> subscribeService.updateSubscribe(testUserId, 100L, updatedSubscribeDto))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(SubscribeErrorCode.SUBSCRIBE_FORBIDDEN);
                    assertThat(businessException.getErrorCode().getMessage()).isEqualTo("해당 구독에 대한 권한이 없습니다.");
                });

        verify(subscribeEditor, never()).updateSubscribe(any(), any(), any(), any());
    }
}
