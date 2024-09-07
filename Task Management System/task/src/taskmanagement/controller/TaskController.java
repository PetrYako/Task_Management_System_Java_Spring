package taskmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TaskController {

    @GetMapping("/api/tasks")
    public ResponseEntity<List<String>> getTasks() {
        List<String> tasks = new ArrayList<>();
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
}
