package org.lostnomore.backend.item.elastic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoriAnalyzerService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUrl;

    public List<String> analyzeKeyword(String keyword) {
        try {
            String analyzeUrl = elasticsearchUrl + "/lost_item/_analyze";
            String requestJson = "{ \"analyzer\": \"nori_analyzer\", \"text\": \"" + keyword + "\" }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(analyzeUrl, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            List<String> tokens = new ArrayList<>();
            for (JsonNode tokenNode : rootNode.path("tokens")) {
                tokens.add(tokenNode.path("token").asText());
            }

            return tokens;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(keyword);
        }
    }
}