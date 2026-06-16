package com.example.courseservice.controller;

import com.example.courseservice.service.McpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpController {

    private final McpService mcpService;

    @GetMapping("/capabilities")
    public ResponseEntity<Map<String, Object>> capabilities() {
        return ResponseEntity.ok(mcpService.getCapabilities());
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> execute(@RequestBody Map<String, Object> body) {
        String tool = (String) body.get("tool");
        Map<String, Object> args = (Map<String, Object>) body.getOrDefault("arguments", Map.of());
        return ResponseEntity.ok(mcpService.executeTool(tool, args));
    }
}
