package taskmanagement.model.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import taskmanagement.model.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.comments WHERE t.author.email = :email")
    List<Task> findByAuthorEmailWithComments(String email, Sort sort);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.comments WHERE t.assignee = :assignee")
    List<Task> findByAssigneeWithComments(String assignee, Sort sort);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.comments WHERE t.author.email = :authorEmail AND t.assignee = :assignee")
    List<Task> findByAuthorEmailAndAssigneeWithComments(String authorEmail, String assignee, Sort sort);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.comments")
    List<Task> findAllWithComments(Sort sort);
}
