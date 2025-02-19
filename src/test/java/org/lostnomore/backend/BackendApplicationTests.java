package org.lostnomore.backend;

import org.junit.jupiter.api.Test;
import org.lostnomore.backend.item.elastic.NoriAnalyzerService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BackendApplicationTests {

	@MockitoBean
	private NoriAnalyzerService noriAnalyzerService;

	@Test
	void contextLoads() {
	}

}
