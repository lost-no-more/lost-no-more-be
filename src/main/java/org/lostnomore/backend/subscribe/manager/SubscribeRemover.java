package org.lostnomore.backend.subscribe.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.subscribe.repository.SubscribeRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SubscribeRemover {

    private final SubscribeRepository subscribeRepository;

    @Modifying
    @Transactional
    @Query("DELETE FROM Subscribe s WHERE s.user.id = :userId")
    public void deleteByUserId(Long userId) {
        subscribeRepository.deleteByUserId(userId);
    }
}
