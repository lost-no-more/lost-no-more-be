package org.lostnomore.backend.subscribe.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.subscribe.repository.SubscribeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeRemover {

    private final SubscribeRepository subscribeRepository;

    public void deleteById(final Long subscribeId) {
        subscribeRepository.deleteById(subscribeId);
    }

    public void deleteByUserId(Long userId) {
        subscribeRepository.deleteByUserId(userId);
    }
}
