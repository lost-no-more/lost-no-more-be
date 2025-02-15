package org.lostnomore.backend.item.repository;

import org.lostnomore.backend.item.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByName(String name);

    List<Location> findByRegion(String region);
}
