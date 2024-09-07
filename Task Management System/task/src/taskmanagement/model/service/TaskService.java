package taskmanagement.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskmanagement.controller.dto.TaskRequest;
import taskmanagement.controller.dto.TaskResponse;
import taskmanagement.model.entity.Task;
import taskmanagement.model.entity.User;
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

    public List<TaskResponse> getTasks(String author, String assignee) {
        List<Task> tasks;
        if (author != null && !author.isBlank() && assignee != null && !assignee.isBlank()) {
            tasks = taskRepository.findByAuthorEmailAndAssigneeAllIgnoreCase(author, assignee, Sort.by(Sort.Direction.DESC, "id"));
        } else if (author != null && !author.isBlank()) {
            tasks = taskRepository.findByAuthorEmailIgnoreCase(author, Sort.by(Sort.Direction.DESC, "id"));
        } else if (assignee != null && !assignee.isBlank()) {
            tasks = taskRepository.findByAssigneeIgnoreCase(assignee, Sort.by(Sort.Direction.DESC, "id"));
        } else {
            tasks = taskRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse createTask(TaskRequest taskRequest, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task(taskRequest.getTitle(), taskRequest.getDescription(), author);
        Task savedTask = taskRepository.save(task);

        return mapToTaskResponse(savedTask);
    }

    public TaskResponse assignTask(Long taskId, String assignee, String currentUserEmail) {
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

    public TaskResponse updateTaskStatus(Long taskId, String newStatus, String currentUserEmail) {
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

    private TaskResponse mapToTaskResponse(Task task) {
        return new TaskResponse(
                String.valueOf(task.getId()),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getAuthor().getEmail().toLowerCase(),
                task.getAssignee()
        );
    }
}
