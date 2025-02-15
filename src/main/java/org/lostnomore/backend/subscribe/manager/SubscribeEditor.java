package org.lostnomore.backend.subscribe.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.dto.request.SubscribeCreateDto;
import org.lostnomore.backend.subscribe.repository.SubscribeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeEditor {

    private final SubscribeRepository subscribeRepository;

    public void updateSubscribe(
            final Subscribe subscribe,
            final String keyword,
            final Category category,
            final String region
            ) {
        subscribe.updateSubscribe(
                keyword,
                category,
                region
        );
    }
}
