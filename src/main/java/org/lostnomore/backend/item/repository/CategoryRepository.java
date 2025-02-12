package org.lostnomore.backend.item.repository;

import org.lostnomore.backend.item.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
