package org.lostnomore.backend.item.repository;

import org.lostnomore.backend.item.domain.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {

}
