package taskmanagement.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
