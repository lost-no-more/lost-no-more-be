package org.lostnomore.backend.item.elastic;

import co.elastic.clients.elasticsearch._types.GeoBounds;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.FieldType;
import co.elastic.clients.elasticsearch._types.query_dsl.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostItemSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final NoriAnalyzerService noriAnalyzerService;

    @Transactional(readOnly = true)
    public SearchHits<LostItemDocument> searchLostItems(
        LocalDate dateStart,
        LocalDate dateEnd,
        Double topLeftLat,
        Double topLeftLon,
        Double bottomRightLat,
        Double bottomRightLon,
        String keyword,
        Long categoryId,
        String region,
        Integer size
    ) {
        List<Query> mustQueries = new ArrayList<>();
        List<Query> shouldQueries = new ArrayList<>();
        log.info("categoryId: {}", categoryId);

        // 날짜 범위
        RangeQuery dateRangeQuery = new RangeQuery.Builder()
            .date(d -> d
                .field("date")
                .gte(dateStart.toString())
                .lte(dateEnd.toString())
            )
            .build();
        mustQueries.add(dateRangeQuery._toQuery());

        // 키워드 검색
        boolean hasKeyword = (keyword != null && !keyword.isBlank());

        if (hasKeyword) {
            List<String> tokens = noriAnalyzerService.analyzeKeyword(keyword);

            if (tokens.size() > 1) {
                // 다중 토큰일 경우 각 토큰을 전부 must로 추가
                for (String token : tokens) {
                    MatchQuery multiTokenMatch = MatchQuery.of(m -> m
                        .field("name.nori")
                        .query(token)
                    );
                    mustQueries.add(multiTokenMatch._toQuery());
                }
            } else {
                MatchQuery singleTokenMatch = MatchQuery.of(m -> m
                    .field("name.nori")
                    .query(keyword)
                );
                shouldQueries.add(singleTokenMatch._toQuery());
            }

            // Edge NGram 기반
            MatchQuery ngramQuery = MatchQuery.of(m -> m
                .field("name.ngram")
                .query(keyword)
            );
            shouldQueries.add(ngramQuery._toQuery());

            // 접두사 기반 검색
            MatchPhrasePrefixQuery phrasePrefix = MatchPhrasePrefixQuery.of(m -> m
                .field("name.nori")
                .query(keyword)
            );
            shouldQueries.add(phrasePrefix._toQuery());

            // Fuzzy 철자 오류 보정
            FuzzyQuery fuzzyQuery = FuzzyQuery.of(f -> f
                .field("name")
                .value(keyword)
                .fuzziness("AUTO")
            );
            shouldQueries.add(fuzzyQuery._toQuery());
        }

        TermQuery termQuery = TermQuery.of(t -> t
            .field("categoryId")
            .value(categoryId)
        );
        mustQueries.add(termQuery._toQuery());

        if (region != null && !region.isBlank()) {
            MatchQuery regionQuery = MatchQuery.of(m -> m
                .field("region")
                .query(region)
            );
            mustQueries.add(regionQuery._toQuery());
        }

        GeoBoundingBoxQuery geoQuery = GeoBoundingBoxQuery.of(g -> g
            .field("location")
            .boundingBox(b -> b
                .tlbr(tlbr -> tlbr
                    .topLeft(tl -> tl.latlon(ll -> ll
                        .lat(topLeftLat)
                        .lon(topLeftLon)
                    ))
                    .bottomRight(br -> br.latlon(ll -> ll
                        .lat(bottomRightLat)
                        .lon(bottomRightLon)
                    ))
                )
            )
        );

        mustQueries.add(geoQuery._toQuery());

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder()
            .must(mustQueries);

        if (hasKeyword) {
            boolQueryBuilder.should(shouldQueries);
            boolQueryBuilder.minimumShouldMatch("1");
        }

        Query boolQuery = boolQueryBuilder.build()._toQuery();

        List<SortOptions> sortOptions = List.of(
            SortOptions.of(s -> s.field(f -> f
                .field("date")
                .order(SortOrder.Desc)
                .unmappedType(FieldType.Date)
            )),
            SortOptions.of(s -> s.field(f -> f
                .field("id")
                .order(SortOrder.Desc)
                .unmappedType(FieldType.Long)
            ))
        );

        int pageSize = (size != null && size > 0) ? size : 1000;

        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .withSort(sortOptions)
            .withPageable(PageRequest.of(0, pageSize))
            .build();

        SearchHits<LostItemDocument> result = elasticsearchOperations.search(searchQuery, LostItemDocument.class);

        return result;
    }

    @Transactional(readOnly = true)
    public SearchHits<LostItemDocument> searchLostItemsForSubscription(
            LocalDate dateStart, LocalDate dateEnd,
            String keyword, Long categoryId, String region, Integer size) {
        return searchLostItems(dateStart, dateEnd, null, null, null, null, keyword, categoryId, region, size);
    }
}
