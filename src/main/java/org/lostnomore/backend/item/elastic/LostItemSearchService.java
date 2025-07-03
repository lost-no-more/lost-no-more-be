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

        log.info("=== 검색 파라미터 ===");
        log.info("categoryId: {}", categoryId);
        log.info("region: {}", region);
        log.info("keyword: {}", keyword);
        log.info("dateStart: {}, dateEnd: {}", dateStart, dateEnd);
        log.info("지리적 범위: topLeft({}, {}), bottomRight({}, {})",
            topLeftLat, topLeftLon, bottomRightLat, bottomRightLon);

        // 날짜 범위
        RangeQuery dateRangeQuery = new RangeQuery.Builder()
            .date(d -> d
                .field("date")
                .gte(dateStart.toString())
                .lte(dateEnd.toString())
            )
            .build();
        mustQueries.add(dateRangeQuery._toQuery());
        log.info("날짜 범위 쿼리 추가: {} ~ {}", dateStart, dateEnd);

        // 키워드 검색
        boolean hasKeyword = (keyword != null && !keyword.isBlank());
        log.info("키워드 검색 여부: {}", hasKeyword);

        if (hasKeyword) {
            List<String> tokens = noriAnalyzerService.analyzeKeyword(keyword);
            log.info("분석된 토큰: {}", tokens);

            if (tokens.size() > 1) {
                // 다중 토큰일 경우 각 토큰을 전부 must로 추가
                for (String token : tokens) {
                    MatchQuery multiTokenMatch = MatchQuery.of(m -> m
                        .field("name.nori")
                        .query(token)
                    );
                    mustQueries.add(multiTokenMatch._toQuery());
                    log.info("다중 토큰 must 쿼리 추가: {}", token);
                }
            } else {
                MatchQuery singleTokenMatch = MatchQuery.of(m -> m
                    .field("name.nori")
                    .query(keyword)
                );
                shouldQueries.add(singleTokenMatch._toQuery());
                log.info("단일 토큰 should 쿼리 추가: {}", keyword);
            }

            // Edge NGram 기반
            MatchQuery ngramQuery = MatchQuery.of(m -> m
                .field("name.ngram")
                .query(keyword)
            );
            shouldQueries.add(ngramQuery._toQuery());
            log.info("NGram should 쿼리 추가: {}", keyword);

            // 접두사 기반 검색
            MatchPhrasePrefixQuery phrasePrefix = MatchPhrasePrefixQuery.of(m -> m
                .field("name.nori")
                .query(keyword)
            );
            shouldQueries.add(phrasePrefix._toQuery());
            log.info("Phrase prefix should 쿼리 추가: {}", keyword);

            // Fuzzy 철자 오류 보정
            FuzzyQuery fuzzyQuery = FuzzyQuery.of(f -> f
                .field("name")
                .value(keyword)
                .fuzziness("AUTO")
            );
            shouldQueries.add(fuzzyQuery._toQuery());
            log.info("Fuzzy should 쿼리 추가: {}", keyword);
        }

        // categoryId 조건부 추가
        if (categoryId != null) {
            TermQuery termQuery = TermQuery.of(t -> t
                .field("categoryId")
                .value(categoryId)
            );
            mustQueries.add(termQuery._toQuery());
            log.info("카테고리 ID must 쿼리 추가: {}", categoryId);
        } else {
            log.info("카테고리 ID가 null이므로 쿼리에서 제외");
        }

        // region 조건부 추가
        if (region != null && !region.isBlank()) {
            MatchQuery regionQuery = MatchQuery.of(m -> m
                .field("region")
                .query(region)
            );
            mustQueries.add(regionQuery._toQuery());
            log.info("지역 must 쿼리 추가: {}", region);
        } else {
            log.info("지역이 null/빈값이므로 쿼리에서 제외");
        }

        // 지리적 범위 조건부 추가
        if (topLeftLat != null && topLeftLon != null &&
            bottomRightLat != null && bottomRightLon != null) {

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
            log.info("지리적 범위 must 쿼리 추가: topLeft({}, {}), bottomRight({}, {})",
                topLeftLat, topLeftLon, bottomRightLat, bottomRightLon);
        } else {
            log.info("지리적 좌표가 null이므로 쿼리에서 제외");
        }

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder()
            .must(mustQueries);

        if (hasKeyword) {
            boolQueryBuilder.should(shouldQueries);
            boolQueryBuilder.minimumShouldMatch("1");
            log.info("should 쿼리 추가됨, minimumShouldMatch: 1");
        }

        Query boolQuery = boolQueryBuilder.build()._toQuery();

        log.info("=== 최종 쿼리 통계 ===");
        log.info("must 쿼리 개수: {}", mustQueries.size());
        log.info("should 쿼리 개수: {}", shouldQueries.size());

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
        log.info("페이지 크기: {}", pageSize);

        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .withSort(sortOptions)
            .withPageable(PageRequest.of(0, pageSize))
            .build();

        // 쿼리 JSON 출력 (가능한 경우)
        try {
            log.info("=== 실행할 Elasticsearch 쿼리 ===");
            log.info("검색 쿼리: {}", searchQuery.getQuery());
            log.info("정렬 옵션: {}", searchQuery.getSort());
        } catch (Exception e) {
            log.warn("쿼리 로깅 중 오류: {}", e.getMessage());
        }

        SearchHits<LostItemDocument> result = elasticsearchOperations.search(searchQuery, LostItemDocument.class);

        log.info("=== 검색 결과 ===");
        log.info("총 검색 결과 수: {}", result.getTotalHits());
        log.info("실제 반환된 문서 수: {}", result.getSearchHits().size());

        return result;
    }

    @Transactional(readOnly = true)
    public SearchHits<LostItemDocument> searchLostItemsForSubscription(
            LocalDate dateStart, LocalDate dateEnd,
            String keyword, Long categoryId, String region, Integer size) {
        return searchLostItems(dateStart, dateEnd, null, null, null, null, keyword, categoryId, region, size);
    }
}
