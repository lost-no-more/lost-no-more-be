package org.lostnomore.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lostnomore.backend.item.elastic.NoriAnalyzerService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class BackendApplicationTests {

	@MockitoBean
	private NoriAnalyzerService noriAnalyzerService;

	@BeforeEach
	void setUp() {
		when(noriAnalyzerService.analyzeKeyword(anyString())).thenReturn(List.of("mocked", "tokens"));
	}

	@Test
	void contextLoads() {
	}

}
