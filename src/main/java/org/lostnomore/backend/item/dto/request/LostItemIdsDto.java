package org.lostnomore.backend.item.dto.request;

import java.util.List;

public record LostItemIdsDto(
        List<Long> ids
) {
}
