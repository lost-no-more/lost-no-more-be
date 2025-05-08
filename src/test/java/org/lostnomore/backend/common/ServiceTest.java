package org.lostnomore.backend.common;

import jakarta.transaction.Transactional;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Transactional
public abstract class ServiceTest {
}