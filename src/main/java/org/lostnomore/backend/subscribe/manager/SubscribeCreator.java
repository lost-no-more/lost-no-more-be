package org.lostnomore.backend.subscribe.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.repository.SubscribeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeCreator {

    private final SubscribeRepository subscribeRepository;


    public void save(final Subscribe subscribe) {
        subscribeRepository.save(subscribe);
    }
}
