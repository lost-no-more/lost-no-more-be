package org.lostnomore.backend.item.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.repository.LocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationRetriever {

    private final LocationRepository locationRepository;

    public Location findByName(final String location) {
        return locationRepository.findByName(location);
    }

    public List<Location> findByRegion(final String region) {
        return locationRepository.findByRegion(region);
    }
}
