package taskmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.controller.dto.TaskRequest;
import taskmanagement.controller.dto.TaskResponse;
import taskmanagement.model.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestParam(required = false) String author) {
        List<TaskResponse> tasks = taskService.getTasks(author);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest, Authentication authentication) {
        if (taskRequest.getTitle() == null || taskRequest.getTitle().isBlank() ||
                taskRequest.getDescription() == null || taskRequest.getDescription().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        TaskResponse taskResponse = taskService.createTask(taskRequest, authentication.getName());
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }
}
