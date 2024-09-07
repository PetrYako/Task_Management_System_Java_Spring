package taskmanagement.model.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import taskmanagement.model.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {
    List<Task> findByAuthorEmailIgnoreCase(String email, Sort sort);
    List<Task> findByAssigneeIgnoreCase(String assignee, Sort sort);
    List<Task> findByAuthorEmailAndAssigneeAllIgnoreCase(String authorEmail, String assignee, Sort sort);
}
