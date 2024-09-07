package taskmanagement.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
