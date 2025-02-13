package org.lostnomore.backend.item.repository;

import org.lostnomore.backend.item.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByName(String name);
}
