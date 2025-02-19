package org.lostnomore.backend.item.elastic;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoriAnalyzerService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String ELASTICSEARCH_URL = "http://localhost:9200/lost_item/_analyze";

    public NoriAnalyzerService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<String> analyzeKeyword(String keyword) {
        try {
            String requestJson = "{ \"analyzer\": \"nori_analyzer\", \"text\": \"" + keyword + "\" }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(ELASTICSEARCH_URL, entity, String.class);

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
