package org.lostnomore.backend.item.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.CategoryErrorCode;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryRetriever {

    private final CategoryRepository categoryRepository;

    public Category findById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Category가 존재하지 않습니다."));
    }

    public Category findByName(final String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new BusinessException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }
}
