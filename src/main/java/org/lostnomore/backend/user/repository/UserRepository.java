package org.lostnomore.backend.user.repository;

import java.util.Optional;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);
}
