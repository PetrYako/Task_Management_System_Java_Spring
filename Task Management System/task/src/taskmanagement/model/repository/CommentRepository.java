package taskmanagement.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.entity.Comment;
import taskmanagement.model.entity.Task;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtDesc(Task task);
}