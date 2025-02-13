package org.lostnomore.backend.auth.repository;

import org.lostnomore.backend.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}