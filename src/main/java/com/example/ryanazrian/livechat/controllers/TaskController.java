package com.example.ryanazrian.livechat.controllers;

import com.example.ryanazrian.livechat.model.EStatus;
import com.example.ryanazrian.livechat.model.Task;
import com.example.ryanazrian.livechat.model.User;
import com.example.ryanazrian.livechat.payload.request.TaskEditRequest;
import com.example.ryanazrian.livechat.payload.request.TaskRequest;
import com.example.ryanazrian.livechat.security.jwt.JwtUtils;
import com.example.ryanazrian.livechat.services.TaskService;
import com.example.ryanazrian.livechat.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TaskService taskService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("/allTask")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> alltask() {
        return new ResponseEntity<>(taskService.getAllTask(), HttpStatus.OK);
    }

    @GetMapping("/taskById")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> taskById(@RequestParam(name = "id") String id) {
        UUID uuid = UUID.fromString(id);
        return new ResponseEntity<>(taskService.getTaskById(uuid), HttpStatus.OK);
    }

    @PostMapping("/addTask")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addTask(@RequestBody @Valid TaskRequest taskRequest, @RequestHeader("Authorization") String token) {

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate and extract user ID from the token
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userDetailsService.getByUsername(username);

        Task newTask = new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                EStatus.TODO,
                user
        );
        taskService.save(newTask);

        return new ResponseEntity<>(newTask, HttpStatus.OK);
    }

    @PatchMapping("/editTask")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> editTask(@RequestBody @Valid TaskEditRequest taskEditRequest, @RequestHeader("Authorization") String token) {
        Task editingTask = taskService.getTaskById(taskEditRequest.getId());

        if (taskEditRequest.getTitle() != null) {
            editingTask.setTitle(taskEditRequest.getTitle());
        }

        if (taskEditRequest.getDescription() != null) {
            editingTask.setDescription(taskEditRequest.getDescription());
        }

        if (taskEditRequest.getStatus() != null) {
            editingTask.setStatus(taskEditRequest.getStatus());
        }

        taskService.save(editingTask);
        return new ResponseEntity<>(editingTask, HttpStatus.OK);
    }

}
