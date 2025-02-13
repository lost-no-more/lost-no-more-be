package org.lostnomore.backend.item.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LostItemSearchRepository extends ElasticsearchRepository<LostItemDocument, Long> {
}
