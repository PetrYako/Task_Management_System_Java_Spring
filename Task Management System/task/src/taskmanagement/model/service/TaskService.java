package taskmanagement.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmanagement.controller.dto.TaskRequest;
import taskmanagement.controller.dto.TaskResponse;
import taskmanagement.model.entity.Task;
import taskmanagement.model.entity.User;
import taskmanagement.model.repository.TaskRepository;
import taskmanagement.model.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public TaskResponse createTask(TaskRequest taskRequest, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task(taskRequest.getTitle(), taskRequest.getDescription(), author);
        Task savedTask = taskRepository.save(task);

        return new TaskResponse(
                String.valueOf(savedTask.getId()),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getStatus(),
                savedTask.getAuthor().getEmail().toLowerCase()
        );
    }
}
