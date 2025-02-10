package org.lostnomore.backend.common;

import org.lostnomore.backend.global.config.JpaConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(JpaConfig.class)
@ActiveProfiles("test")
public abstract class RepositoryTest {
}