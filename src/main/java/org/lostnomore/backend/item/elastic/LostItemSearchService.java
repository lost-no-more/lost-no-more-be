package org.lostnomore.backend.item.elastic;

import co.elastic.clients.elasticsearch._types.GeoBounds;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class LostItemSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final NoriAnalyzerService noriAnalyzerService;

    // @Transactional(readOnly = true) // 트랜잭션 제거
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

        System.out.println("========== 검색 시작 ==========");
        System.out.println("파라미터들:");
        System.out.println("dateStart: " + dateStart);
        System.out.println("dateEnd: " + dateEnd);
        System.out.println("keyword: " + keyword);
        System.out.println("categoryId: " + categoryId);
        System.out.println("region: " + region);
        System.out.println("topLeftLat: " + topLeftLat);
        System.out.println("topLeftLon: " + topLeftLon);
        System.out.println("bottomRightLat: " + bottomRightLat);
        System.out.println("bottomRightLon: " + bottomRightLon);
        System.out.println("size: " + size);

        try {
            List<Query> mustQueries = new ArrayList<>();
            List<Query> shouldQueries = new ArrayList<>();

            System.out.println("1. 날짜 범위 쿼리 생성 중...");
            // 날짜 범위
            RangeQuery dateRangeQuery = new RangeQuery.Builder()
                .date(d -> d
                    .field("date")
                    .gte(dateStart.toString())
                    .lte(dateEnd.toString())
                )
                .build();
            mustQueries.add(dateRangeQuery._toQuery());
            System.out.println("날짜 범위 쿼리 생성 완료");

            // 키워드 검색
            boolean hasKeyword = (keyword != null && !keyword.isBlank());
            System.out.println("2. 키워드 검색 여부: " + hasKeyword);

            if (hasKeyword) {
                System.out.println("키워드 분석 시작...");
                List<String> tokens = noriAnalyzerService.analyzeKeyword(keyword);
                System.out.println("분석된 토큰들: " + tokens);

                if (tokens.size() > 1) {
                    System.out.println("다중 토큰 처리 중...");
                    // 다중 토큰일 경우 각 토큰을 전부 must로 추가
                    for (String token : tokens) {
                        MatchQuery multiTokenMatch = MatchQuery.of(m -> m
                            .field("name.nori")
                            .query(token)
                        );
                        mustQueries.add(multiTokenMatch._toQuery());
                        System.out.println("토큰 추가: " + token);
                    }
                } else {
                    System.out.println("단일 토큰 처리 중...");
                    // 단일 토큰일 경우 기존처럼 should에 추가
                    MatchQuery singleTokenMatch = MatchQuery.of(m -> m
                        .field("name.nori")
                        .query(keyword)
                    );
                    shouldQueries.add(singleTokenMatch._toQuery());
                    System.out.println("단일 토큰 should 쿼리 추가");
                }

                System.out.println("NGram 쿼리 추가 중...");
                // Edge NGram 기반
                MatchQuery ngramQuery = MatchQuery.of(m -> m
                    .field("name.ngram")
                    .query(keyword)
                );
                shouldQueries.add(ngramQuery._toQuery());

                System.out.println("Phrase Prefix 쿼리 추가 중...");
                // 접두사 기반 검색
                MatchPhrasePrefixQuery phrasePrefix = MatchPhrasePrefixQuery.of(m -> m
                    .field("name.nori")
                    .query(keyword)
                );
                shouldQueries.add(phrasePrefix._toQuery());

                System.out.println("Fuzzy 쿼리 추가 중...");
                // Fuzzy 철자 오류 보정
                FuzzyQuery fuzzyQuery = FuzzyQuery.of(f -> f
                    .field("name")
                    .value(keyword)
                    .fuzziness("AUTO")
                );
                shouldQueries.add(fuzzyQuery._toQuery());
                System.out.println("키워드 관련 쿼리들 모두 생성 완료");
            }

            // 카테고리
            System.out.println("3. 카테고리 필터 확인...");
            if (categoryId != null) {
                System.out.println("카테고리 쿼리 추가: " + categoryId);
                TermQuery termQuery = TermQuery.of(t -> t
                    .field("categoryId")
                    .value(categoryId)
                );
                mustQueries.add(termQuery._toQuery());
            } else {
                System.out.println("카테고리 필터 없음");
            }

            // 지역
            System.out.println("4. 지역 필터 확인...");
            if (region != null && !region.isBlank()) {
                System.out.println("지역 쿼리 추가: " + region);
                MatchQuery regionQuery = MatchQuery.of(m -> m
                    .field("region")
                    .query(region)
                );
                mustQueries.add(regionQuery._toQuery());
            } else {
                System.out.println("지역 필터 없음");
            }

            // === 단계별 테스트 시작 ===
            System.out.println("=== 단계별 테스트 시작 ===");

            // 1단계: 날짜 + 카테고리만 (위치 쿼리 제외)
            System.out.println("1단계 테스트: 날짜 + 카테고리만 (위치 제외)");
            try {
                BoolQuery testBoolQuery = new BoolQuery.Builder()
                    .must(mustQueries) // 현재까지의 쿼리들 (위치 제외)
                    .build();

                NativeQuery testQuery = NativeQuery.builder()
                    .withQuery(testBoolQuery._toQuery())
                    .withPageable(PageRequest.of(0, 10)) // 작은 크기로 테스트
                    .build();

                SearchHits<LostItemDocument> testResult = elasticsearchOperations.search(testQuery, LostItemDocument.class);
                System.out.println("1단계 성공! 결과 수: " + testResult.getTotalHits());

                // 1단계가 성공하면 정렬 추가 테스트
                System.out.println("2단계 테스트: 정렬 추가");
                List<SortOptions> sortOptions = List.of(
                    SortOptions.of(s -> s.field(f -> f.field("date").order(SortOrder.Desc))),
                    SortOptions.of(s -> s.field(f -> f.field("id").order(SortOrder.Desc)))
                );

                NativeQuery testQueryWithSort = NativeQuery.builder()
                    .withQuery(testBoolQuery._toQuery())
                    .withSort(sortOptions) // 정렬 추가
                    .withPageable(PageRequest.of(0, 10))
                    .build();

                SearchHits<LostItemDocument> testResultWithSort = elasticsearchOperations.search(testQueryWithSort, LostItemDocument.class);
                System.out.println("2단계 성공! 정렬 포함 결과 수: " + testResultWithSort.getTotalHits());

                // 여기까지 성공하면 위치 쿼리가 문제임
                // 위치 쿼리 없이 최종 검색 실행
                int pageSize = (size != null && size > 0) ? size : 1000;

                BoolQuery.Builder finalBoolQueryBuilder = new BoolQuery.Builder()
                    .must(mustQueries);

                if (hasKeyword) {
                    finalBoolQueryBuilder.should(shouldQueries);
                    finalBoolQueryBuilder.minimumShouldMatch("1");
                }

                Query finalBoolQuery = finalBoolQueryBuilder.build()._toQuery();

                NativeQuery finalQuery = NativeQuery.builder()
                    .withQuery(finalBoolQuery)
                    .withSort(sortOptions)
                    .withPageable(PageRequest.of(0, pageSize))
                    .build();

                SearchHits<LostItemDocument> finalResult = elasticsearchOperations.search(finalQuery, LostItemDocument.class);

                System.out.println("========== 검색 성공 (위치 제외)! ==========");
                System.out.println("총 결과 수: " + finalResult.getTotalHits());
                System.out.println("반환된 문서 수: " + finalResult.getSearchHits().size());
                System.out.println("경고: 위치 쿼리는 제외되었습니다.");
                System.out.println("=====================================");

                return finalResult;

            } catch (Exception e1) {
                System.out.println("1단계 실패: " + e1.getMessage());

                // 1단계도 실패하면 더 간단한 테스트
                System.out.println("기본 테스트: 날짜만");
                try {
                    List<Query> dateOnlyQueries = new ArrayList<>();
                    dateOnlyQueries.add(dateRangeQuery._toQuery());

                    BoolQuery simpleBoolQuery = new BoolQuery.Builder()
                        .must(dateOnlyQueries)
                        .build();

                    NativeQuery simpleQuery = NativeQuery.builder()
                        .withQuery(simpleBoolQuery._toQuery())
                        .withPageable(PageRequest.of(0, 10))
                        .build();

                    SearchHits<LostItemDocument> simpleResult = elasticsearchOperations.search(simpleQuery, LostItemDocument.class);
                    System.out.println("기본 테스트 성공! 결과 수: " + simpleResult.getTotalHits());
                    return simpleResult;

                } catch (Exception e2) {
                    System.out.println("기본 테스트도 실패: " + e2.getMessage());
                    throw e2;
                }
            }

        } catch (Exception e) {
            System.out.println("========== 검색 실패! ==========");
            System.out.println("오류 타입: " + e.getClass().getSimpleName());
            System.out.println("오류 메시지: " + e.getMessage());
            System.out.println("스택 트레이스:");
            e.printStackTrace();
            System.out.println("===============================");
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public SearchHits<LostItemDocument> searchLostItemsForSubscription(
            LocalDate dateStart, LocalDate dateEnd,
            String keyword, Long categoryId, String region, Integer size) {
        return searchLostItems(dateStart, dateEnd, null, null, null, null, keyword, categoryId, region, size);
    }
}
