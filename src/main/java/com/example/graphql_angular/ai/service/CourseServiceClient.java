package com.example.graphql_angular.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CourseServiceClient {

    private final RestTemplate rest = new RestTemplate();
    private final String baseUrl = "http://localhost:8081";

    public List<Map<String, Object>> getTools() {
        try {
            Map<String, Object> caps = rest.getForObject(baseUrl + "/mcp/capabilities", Map.class);
            if (caps != null && caps.containsKey("tools")) {
                Object tools = caps.get("tools");
                if (tools instanceof List) {
                    return (List<Map<String, Object>>) tools;
                }
            }
        } catch (Exception e) {
            // course-service not available
        }
        return List.of();
    }

    public Map<String, Object> executeTool(String toolName, Map<String, Object> args) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("tool", toolName);
            body.put("arguments", args);
            return rest.postForObject(baseUrl + "/mcp/execute", body, Map.class);
        } catch (Exception e) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("tool", toolName);
            err.put("status", "error");
            err.put("error", "Course service unavailable: " + e.getMessage());
            return err;
        }
    }

    public List<Map<String, Object>> getAllCourses() {
        try {
            List list = rest.getForObject(baseUrl + "/api/courses", List.class);
            return list != null ? list : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
