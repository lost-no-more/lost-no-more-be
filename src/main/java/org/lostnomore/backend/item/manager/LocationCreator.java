package org.lostnomore.backend.item.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.repository.LocationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationCreator {

    private final LocationRepository locationRepository;

    public Location save(final Location location) {
        return locationRepository.save(location);
    }
}
