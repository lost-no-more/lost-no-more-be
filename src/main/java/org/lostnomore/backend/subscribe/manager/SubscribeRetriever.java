package org.lostnomore.backend.subscribe.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.repository.SubscribeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscribeRetriever {

    private final SubscribeRepository subscribeRepository;

    public List<Subscribe> findByUserId(final Long userId) {
        return subscribeRepository.findByUserId(userId);
    }
}
