package org.lostnomore.backend.subscribe.repository;

import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

}
