package taskmanagement.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskmanagement.controller.dto.CommentResponse;
import taskmanagement.controller.dto.TaskRequest;
import taskmanagement.controller.dto.TaskResponse;
import taskmanagement.controller.dto.TaskWithoutTotalCommentsResponse;
import taskmanagement.model.entity.Comment;
import taskmanagement.model.entity.Task;
import taskmanagement.model.entity.User;
import taskmanagement.model.repository.CommentRepository;
import taskmanagement.model.repository.TaskRepository;
import taskmanagement.model.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentResponse> getComments(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        List<Comment> comments = commentRepository.findByTaskOrderByCreatedAtDesc(task);

        return comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId().toString(),
                        task.getId().toString(),
                        comment.getText(),
                        comment.getAuthor().getEmail()
                ))
                .collect(Collectors.toList());
    }

    public void addComment(Long taskId, String commentText, String authorEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User author = userRepository.findByEmail(authorEmail.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = new Comment(commentText, task, author);
        commentRepository.save(comment);
    }

    public List<TaskResponse> getTasks(String author, String assignee) {
        List<Task> tasks;
        if (author != null && !author.isBlank() && assignee != null && !assignee.isBlank()) {
            tasks = taskRepository.findByAuthorEmailAndAssigneeWithComments(author, assignee, Sort.by(Sort.Direction.DESC, "id"));
        } else if (author != null && !author.isBlank()) {
            tasks = taskRepository.findByAuthorEmailWithComments(author, Sort.by(Sort.Direction.DESC, "id"));
        } else if (assignee != null && !assignee.isBlank()) {
            tasks = taskRepository.findByAssigneeWithComments(assignee, Sort.by(Sort.Direction.DESC, "id"));
        } else {
            tasks = taskRepository.findAllWithComments(Sort.by(Sort.Direction.DESC, "id"));
        }
        return tasks.stream()
                .map(this::mapToTaskResponseWithComments)
                .collect(Collectors.toList());
    }

    public TaskWithoutTotalCommentsResponse createTask(TaskRequest taskRequest, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task(taskRequest.getTitle(), taskRequest.getDescription(), author);
        Task savedTask = taskRepository.save(task);

        return mapToTaskResponse(savedTask);
    }

    public TaskWithoutTotalCommentsResponse assignTask(Long taskId, String assignee, String currentUserEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getAuthor().getEmail().equals(currentUserEmail)) {
            throw new IllegalStateException("Only the author can assign the task");
        }

        if (assignee.equals("none")) {
            task.setAssignee("none");
        } else {
            User assigneeUser = userRepository.findByEmail(assignee.toLowerCase())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            task.setAssignee(assigneeUser.getEmail());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    public TaskWithoutTotalCommentsResponse updateTaskStatus(Long taskId, String newStatus, String currentUserEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getAuthor().getEmail().equals(currentUserEmail) &&
                !task.getAssignee().equals(currentUserEmail)) {
            throw new IllegalStateException("Only the author or assignee can update the task status");
        }

        task.setStatus(Task.TaskStatus.valueOf(newStatus));
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    private TaskWithoutTotalCommentsResponse mapToTaskResponse(Task task) {
        return new TaskWithoutTotalCommentsResponse(
                String.valueOf(task.getId()),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getAuthor().getEmail().toLowerCase(),
                task.getAssignee()
        );
    }

    private TaskResponse mapToTaskResponseWithComments(Task task) {
        return new TaskResponse(
                String.valueOf(task.getId()),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().toString(),
                task.getAuthor().getEmail(),
                task.getAssignee(),
                task.getTotalComments()
        );
    }
}
