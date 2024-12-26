package com.example.ryanazrian.livechat.services;

import com.example.ryanazrian.livechat.model.Task;
import com.example.ryanazrian.livechat.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public List<Task> getAllTask () {
        return taskRepository.findAll();
    }

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public Task save(Task task) {
        taskRepository.save(task);

        return task;
    }

    public Task updateTask(UUID taskId, Task task) {
        Task updatingTask = taskRepository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found"));
        updatingTask.setStatus(task.getStatus());
        taskRepository.save(updatingTask);

        return updatingTask;
    }

    public void deleteTask(UUID taskId) {
        taskRepository.deleteById(taskId);
    }
}
