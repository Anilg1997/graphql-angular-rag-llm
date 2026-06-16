package com.example.graphql_angular.ai.controller;

import com.example.graphql_angular.ai.service.CourseServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseProxyController {

    private final CourseServiceClient courseClient;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        return ResponseEntity.ok(courseClient.getAllCourses());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = courseClient.executeTool("createCourse", body);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        courseClient.executeTool("deleteCourse", Map.of("id", id));
        return ResponseEntity.ok("Deleted");
    }
}
