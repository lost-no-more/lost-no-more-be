package org.lostnomore.backend.item.repository;

import jakarta.persistence.Tuple;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.dto.request.LostItemIdsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    @Query("""
            SELECT 
                COUNT (case when l.createdDate >= :today THEN 1 END) as todayCount,
                COUNT(l) as totalCount
            FROM LostItem l
            """)
    Tuple findItemCountByCreatedAtAfter(@Param("today") LocalDateTime today);

    @Query("""
       SELECT l FROM LostItem l
       JOIN FETCH l.location
       WHERE l.category IN (
           SELECT s.category FROM Subscribe s WHERE s.user.id = :userId
       )
       ORDER BY l.createdDate DESC
       """)
    Page<LostItem> findRecentItemsByUserId(Long userId, Pageable pageable);


    @Query("""
       SELECT l FROM LostItem l
       JOIN FETCH l.location
       JOIN FETCH l.category
       WHERE l.id IN :ids
       """)
    List<LostItem> findByIdIn(List<Long> ids);

    @Query("""
      SELECT l FROM LostItem l
      JOIN FETCH l.location
      JOIN FETCH l.category
      WHERE l.id = :lostItemId
      """)
    Optional<LostItem> findById(Long lostItemId);
}
