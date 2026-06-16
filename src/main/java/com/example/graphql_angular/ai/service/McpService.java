package com.example.graphql_angular.ai.service;

import com.example.graphql_angular.ai.tool.StudentTools;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class McpService {

    private final StudentTools studentTools;
    private final CourseServiceClient courseClient;

    private List<Map<String, Object>> getStudentTools() {
        return List.of(
            Map.of("name", "getAllStudents", "description", "List all students", "service", "student-service", "parameters", List.of()),
            Map.of("name", "getStudent", "description", "Get student by ID", "service", "student-service", "parameters", List.of(Map.of("name", "id", "type", "string", "required", true))),
            Map.of("name", "createStudent", "description", "Create new student", "service", "student-service", "parameters", List.of(
                Map.of("name", "name", "type", "string", "required", true),
                Map.of("name", "email", "type", "string", "required", true),
                Map.of("name", "course", "type", "string", "required", true))),
            Map.of("name", "updateStudent", "description", "Update student", "service", "student-service", "parameters", List.of(
                Map.of("name", "id", "type", "string", "required", true),
                Map.of("name", "name", "type", "string", "required", true),
                Map.of("name", "email", "type", "string", "required", true),
                Map.of("name", "course", "type", "string", "required", true))),
            Map.of("name", "deleteStudent", "description", "Delete student", "service", "student-service", "parameters", List.of(
                Map.of("name", "id", "type", "string", "required", true)))
        );
    }

    public Map<String, Object> getCapabilities() {
        Map<String, Object> caps = new LinkedHashMap<>();
        caps.put("protocol", "model-context-protocol");
        caps.put("version", "0.1.0");
        caps.put("description", "MCP gateway aggregating tools from student-service and course-service");

        List<Map<String, Object>> allTools = new ArrayList<>();
        allTools.addAll(getStudentTools());

        List<Map<String, Object>> courseTools = courseClient.getTools();
        for (Map<String, Object> t : courseTools) {
            Map<String, Object> enriched = new LinkedHashMap<>(t);
            enriched.put("service", "course-service");
            allTools.add(enriched);
        }

        caps.put("tools", allTools);
        caps.put("services", List.of(
            Map.of("name", "student-service", "url", "http://localhost:8080"),
            Map.of("name", "course-service", "url", "http://localhost:8081")
        ));
        return caps;
    }

    public Map<String, Object> executeTool(String toolName, Map<String, Object> args) {
        // Route to course-service if it's a course tool
        if (toolName.startsWith("get") && toolName.contains("Course")
            || toolName.startsWith("create") && toolName.contains("Course")
            || toolName.startsWith("update") && toolName.contains("Course")
            || toolName.startsWith("delete") && toolName.contains("Course")) {
            return courseClient.executeTool(toolName, args);
        }

        // Handle student tools locally
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tool", toolName);
        try {
            Object output = switch (toolName) {
                case "getAllStudents" -> studentTools.getAllStudents();
                case "getStudent" -> studentTools.getStudent((String) args.get("id"));
                case "createStudent" -> studentTools.createStudent(
                    (String) args.get("name"), (String) args.get("email"), (String) args.get("course"));
                case "updateStudent" -> studentTools.updateStudent(
                    (String) args.get("id"), (String) args.get("name"), (String) args.get("email"), (String) args.get("course"));
                case "deleteStudent" -> studentTools.deleteStudent((String) args.get("id"));
                default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
            };
            result.put("status", "success");
            result.put("output", output);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> processQuery(String query) {
        String lower = query.toLowerCase();
        List<Map<String, Object>> results = new ArrayList<>();
        if (lower.contains("all") || lower.contains("list") || lower.contains("students")) {
            results.add(executeTool("getAllStudents", Map.of()));
        } else if (lower.contains("course")) {
            results.add(executeTool("getAllCourses", Map.of()));
        } else if (lower.contains("create") || lower.contains("add")) {
            results.add(Map.of("status", "info", "message", "Use: createStudent(name,email,course) or createCourse(title,description,instructor,credits)", "tool", "help"));
        } else {
            results.add(Map.of("status", "info", "message", "Available: student tools & course tools", "tool", "help"));
        }
        return results;
    }
}
