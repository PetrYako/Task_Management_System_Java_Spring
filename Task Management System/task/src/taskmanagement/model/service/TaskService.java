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

    public List<TaskResponse> getTasks(String author) {
        List<Task> tasks;
        if (author != null && !author.isBlank()) {
            tasks = taskRepository.findByAuthorEmail(author.toLowerCase(), Sort.by(Sort.Direction.DESC, "id"));
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

    private TaskResponse mapToTaskResponse(Task task) {
        return new TaskResponse(
                String.valueOf(task.getId()),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor().getEmail().toLowerCase()
        );
    }
}
