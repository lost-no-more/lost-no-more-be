package org.lostnomore.backend.subscribe.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.SubscribeErrorCode;
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

    public Subscribe findById(Long subscribeId) {
        return subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new BusinessException(SubscribeErrorCode.SUBSCRIBE_NOT_FOUND));
    }
}
