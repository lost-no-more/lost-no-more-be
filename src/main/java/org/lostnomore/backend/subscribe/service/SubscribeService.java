package org.lostnomore.backend.subscribe.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.subscribe.manager.SubscribeRemover;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRemover subscribeRemover;

    public void deleteByUserId(Long userId) {
        subscribeRemover.deleteByUserId(userId);
    }
}
