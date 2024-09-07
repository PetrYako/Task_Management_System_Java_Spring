package taskmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.controller.dto.AssignRequest;
import taskmanagement.controller.dto.StatusUpdateRequest;
import taskmanagement.controller.dto.TaskRequest;
import taskmanagement.controller.dto.TaskResponse;
import taskmanagement.model.service.TaskService;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee) {
        List<TaskResponse> tasks = taskService.getTasks(author, assignee);
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

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long taskId,
                                                   @RequestBody AssignRequest assignRequest,
                                                   Authentication authentication) {
        String assignee = assignRequest.getAssignee();
        if (assignee == null || (!assignee.equals("none") && !EMAIL_REGEX.matcher(assignee).matches())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            TaskResponse taskResponse = taskService.assignTask(taskId, assignee, authentication.getName());
            return ResponseEntity.ok(taskResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long taskId,
                                                         @RequestBody StatusUpdateRequest statusUpdateRequest,
                                                         Authentication authentication) {
        String newStatus = statusUpdateRequest.getStatus();
        if (newStatus == null || !isValidStatus(newStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            TaskResponse taskResponse = taskService.updateTaskStatus(taskId, newStatus, authentication.getName());
            return ResponseEntity.ok(taskResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private boolean isValidStatus(String status) {
        return status.equals("CREATED") || status.equals("IN_PROGRESS") || status.equals("COMPLETED");
    }
}
