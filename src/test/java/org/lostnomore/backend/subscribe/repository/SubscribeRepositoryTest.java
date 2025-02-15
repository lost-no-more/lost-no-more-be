package org.lostnomore.backend.subscribe.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lostnomore.backend.common.RepositoryTest;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.repository.CategoryRepository;
import org.lostnomore.backend.item.repository.LocationRepository;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SubscribeRepositoryTest extends RepositoryTest {

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User testUser;
    private Category testCategory;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(
                User.builder().name("테스트 유저").email("test@gmail.com").socialType(SocialType.KAKAO).build()
        );
        testCategory = categoryRepository.save(Category.builder().name("지갑").build());
        testLocation = locationRepository.save(
                Location.builder().name("서울경찰서").region("서울").latitude(37.125).longitude(127.124).build()
        );
    }

    @Test
    @DisplayName("구독이 정상적으로 생성된다.")
    void createSubscribeTest() {
        // Given
        Subscribe subscribe = Subscribe.builder()
                .user(testUser)
                .keyword("고양이지갑")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        // When
        Subscribe savedSubscribe = subscribeRepository.save(subscribe);

        // Then
        Optional<Subscribe> foundSubscribe = subscribeRepository.findById(savedSubscribe.getId());
        assertThat(foundSubscribe).isPresent();
        assertThat(foundSubscribe.get().getKeyword()).isEqualTo("고양이지갑");
        assertThat(foundSubscribe.get().getUser()).isEqualTo(testUser);
        assertThat(foundSubscribe.get().getCategory()).isEqualTo(testCategory);
        assertThat(foundSubscribe.get().getRegion()).isEqualTo(testLocation.getRegion());
    }

    @Test
    @DisplayName("같은 사용자가 동일한 키워드와 지역으로 중복 구독하면 예외가 발생한다.")
    void createSubscribe_DuplicateTest() {
        // Given
        Subscribe subscribe1 = Subscribe.builder()
                .user(testUser)
                .keyword("고양이지갑")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        Subscribe subscribe2 = Subscribe.builder()
                .user(testUser)
                .keyword("고양이지갑")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        subscribeRepository.save(subscribe1);
//        entityManager.flush();
//        entityManager.clear();

        // When & Then
        assertThatThrownBy(() -> subscribeRepository.save(subscribe2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("특정 사용자의 구독 목록을 조회한다.")
    void getSubscribesTest() {
        // Given
        Subscribe subscribe1 = Subscribe.builder()
                .user(testUser)
                .keyword("노트북")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        Subscribe subscribe2 = Subscribe.builder()
                .user(testUser)
                .keyword("에어팟")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        subscribeRepository.save(subscribe1);
        subscribeRepository.save(subscribe2);

        // When
        List<Subscribe> subscribes = subscribeRepository.findByUserId(testUser.getId());

        // Then
        assertThat(subscribes.size()).isEqualTo(2);
        assertThat(subscribes).extracting("keyword").containsExactlyInAnyOrder("노트북", "에어팟");
    }

    @Test
    @DisplayName("구독을 삭제하면 데이터베이스에서 제거된다.")
    void deleteSubscribeTest() {
        // Given
        Subscribe subscribe = Subscribe.builder()
                .user(testUser)
                .keyword("노트북")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        Subscribe savedSubscribe = subscribeRepository.save(subscribe);

        // When
        subscribeRepository.deleteById(savedSubscribe.getId());

        // Then
        Optional<Subscribe> foundSubscribe = subscribeRepository.findById(savedSubscribe.getId());
        assertThat(foundSubscribe.isPresent()).isFalse();
    }

    @Test
    @DisplayName("구독 정보를 수정할 수 있다.")
    void updateSubscribeTest() {
        // Given
        Subscribe subscribe = Subscribe.builder()
                .user(testUser)
                .keyword("노트북")
                .category(testCategory)
                .region(testLocation.getRegion())
                .build();

        Subscribe savedSubscribe = subscribeRepository.save(subscribe);

        // When
        Subscribe foundSubscribe = subscribeRepository.findById(savedSubscribe.getId()).orElseThrow();
        foundSubscribe.updateSubscribe("아이패드", testCategory, "부산");

        // Then
        Subscribe updatedSubscribe = subscribeRepository.findById(savedSubscribe.getId()).orElseThrow();
        assertThat(updatedSubscribe.getKeyword()).isEqualTo("아이패드");
    }
}