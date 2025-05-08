package org.lostnomore.backend;

import org.junit.jupiter.api.Test;
import org.lostnomore.backend.item.elastic.LostItemSearchRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

	@MockitoBean
	private LostItemSearchRepository lostItemSearchRepository;

	@Test
	void contextLoads() {
	}
}