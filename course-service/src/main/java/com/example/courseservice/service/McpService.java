package com.example.courseservice.service;

import com.example.courseservice.tool.CourseTools;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class McpService {

    private final CourseTools courseTools;

    public Map<String, Object> getCapabilities() {
        return Map.of(
            "protocol", "model-context-protocol",
            "version", "0.1.0",
            "service", "course-service",
            "tools", List.of(
                Map.of("name", "getAllCourses", "description", "List all courses", "parameters", List.of()),
                Map.of("name", "getCourse", "description", "Get course by ID", "parameters", List.of(Map.of("name", "id", "type", "string", "required", true))),
                Map.of("name", "createCourse", "description", "Create a new course", "parameters", List.of(
                    Map.of("name", "title", "type", "string", "required", true),
                    Map.of("name", "description", "type", "string", "required", true),
                    Map.of("name", "instructor", "type", "string", "required", true),
                    Map.of("name", "credits", "type", "number", "required", true))),
                Map.of("name", "updateCourse", "description", "Update a course", "parameters", List.of(
                    Map.of("name", "id", "type", "string", "required", true),
                    Map.of("name", "title", "type", "string", "required", true),
                    Map.of("name", "description", "type", "string", "required", true),
                    Map.of("name", "instructor", "type", "string", "required", true),
                    Map.of("name", "credits", "type", "number", "required", true))),
                Map.of("name", "deleteCourse", "description", "Delete a course by ID", "parameters", List.of(Map.of("name", "id", "type", "string", "required", true)))
            ));
    }

    public Map<String, Object> executeTool(String toolName, Map<String, Object> args) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tool", toolName);
        try {
            Object output = switch (toolName) {
                case "getAllCourses" -> courseTools.getAllCourses();
                case "getCourse" -> courseTools.getCourse((String) args.get("id"));
                case "createCourse" -> courseTools.createCourse(
                    (String) args.get("title"),
                    (String) args.get("description"),
                    (String) args.get("instructor"),
                    args.get("credits") instanceof Number n ? n.intValue() : 0);
                case "updateCourse" -> courseTools.updateCourse(
                    (String) args.get("id"),
                    (String) args.get("title"),
                    (String) args.get("description"),
                    (String) args.get("instructor"),
                    args.get("credits") instanceof Number n ? n.intValue() : 0);
                case "deleteCourse" -> courseTools.deleteCourse((String) args.get("id"));
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
}
