package com.example.graphql_angular.ai.service;

import com.example.graphql_angular.ai.model.AiResponse;
import com.example.graphql_angular.ai.tool.StudentTools;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final LlmService llmService;
    private final StudentTools studentTools;
    private final RagService ragService;
    private final CourseServiceClient courseClient;

    public AiResponse processAgentTask(String userInput) {
        String lowerInput = userInput.toLowerCase();

        if (lowerInput.contains("course")) {
            return handleCourseTask(userInput);
        }

        if (isCrudRequest(lowerInput)) {
            return handleCrudTask(userInput);
        }

        if (isRagRequest(lowerInput)) {
            return ragService.ask(userInput);
        }

        return handleGeneralChat(userInput);
    }

    private boolean isCrudRequest(String input) {
        return input.contains("student") || input.contains("create") || input.contains("delete")
                || input.contains("update") || input.contains("list") || input.contains("show")
                || input.contains("all") || input.contains("get");
    }

    private boolean isRagRequest(String input) {
        return input.contains("document") || input.contains("context") || input.contains("rag")
                || input.contains("search") || input.contains("find about");
    }

    private AiResponse handleCourseTask(String userInput) {
        String lower = userInput.toLowerCase();
        Object result;

        if (lower.contains("list") || lower.contains("all") || lower.contains("show")) {
            List<Map<String, Object>> courses = courseClient.getAllCourses();
            String details = courses.stream()
                .map(c -> c.get("title") + " (" + c.get("credits") + " credits) - " + c.get("instructor"))
                .reduce((a, b) -> a + "\n" + b)
                .orElse("No courses found");
            result = courses.size() + " courses:\n" + details;
        } else if (lower.contains("create") || lower.contains("add") || lower.contains("new")) {
            Pattern p = Pattern.compile("title\\s+(.+?)(?:\\s+description\\s+(.+?))?(?:\\s+instructor\\s+(.+?))?(?:\\s+credits\\s+(\\d+))?", Pattern.DOTALL);
            Matcher m = p.matcher(userInput);
            if (m.find()) {
                String title = m.group(1) != null ? m.group(1).trim() : "Untitled";
                String desc = m.group(2) != null ? m.group(2).trim() : "";
                String instructor = m.group(3) != null ? m.group(3).trim() : "TBD";
                int credits = m.group(4) != null ? Integer.parseInt(m.group(4).trim()) : 3;
                result = courseClient.executeTool("createCourse", Map.of(
                    "title", title, "description", desc, "instructor", instructor, "credits", credits));
            } else {
                result = "Format: create course title X description Y instructor Z credits N";
            }
        } else if (lower.contains("delete") || lower.contains("remove")) {
            Pattern p = Pattern.compile("id\\s+(\\w+)");
            Matcher m = p.matcher(userInput);
            if (m.find()) {
                result = courseClient.executeTool("deleteCourse", Map.of("id", m.group(1)));
            } else {
                result = "Format: delete course id X";
            }
        } else {
            result = "Available course commands: list courses, create course, delete course id X";
        }

        String systemPrompt = "You are an AI assistant managing course records. Respond helpfully.";
        String llmResponse = llmService.chatWithContext(systemPrompt, userInput);

        return new AiResponse(
            llmResponse + "\n\nResult: " + result,
            List.of("Course Service"),
            Map.of("type", "course")
        );
    }

    private AiResponse handleCrudTask(String userInput) {
        String systemPrompt = "You are an AI assistant that manages student records. "
                + "Based on the user's request, determine which tool to use and respond appropriately.\n\n"
                + studentTools.getToolsDescription()
                + "\n\nCurrent student data:\n" + studentTools.getAllStudents()
                + "\n\nAvailable courses:\n" + courseClient.getAllCourses();

        String llmResponse = llmService.chatWithContext(systemPrompt, userInput);

        String action = extractAction(userInput);
        Object result = executeAction(action, userInput);

        return new AiResponse(
                llmResponse + "\n\nAction Result: " + result,
                List.of("Student Database"),
                Map.of("action", action, "type", "crud")
        );
    }

    private String extractAction(String input) {
        String lower = input.toLowerCase();
        if (lower.contains("create") || lower.contains("add") || lower.contains("new"))
            return "create";
        if (lower.contains("delete") || lower.contains("remove"))
            return "delete";
        if (lower.contains("update") || lower.contains("edit") || lower.contains("change"))
            return "update";
        if (lower.contains("list") || lower.contains("all") || lower.contains("show"))
            return "list";
        return "chat";
    }

    private Object executeAction(String action, String input) {
        return switch (action) {
            case "list" -> studentTools.getAllStudents();
            case "create" -> {
                Pattern p = Pattern.compile("name\\s+(\\w+).*?email\\s+([\\w.@]+).*?course\\s+(\\w+)", Pattern.DOTALL);
                Matcher m = p.matcher(input);
                if (m.find()) {
                    yield studentTools.createStudent(m.group(1), m.group(2), m.group(3));
                }
                yield "Could not parse student details. Format: name X email Y course Z";
            }
            case "delete" -> {
                Pattern p = Pattern.compile("id\\s+(\\w+)");
                Matcher m = p.matcher(input);
                if (m.find()) {
                    yield studentTools.deleteStudent(m.group(1));
                }
                yield "Could not parse ID. Format: delete id X";
            }
            default -> "No specific action matched";
        };
    }

    private AiResponse handleGeneralChat(String userInput) {
        String response = llmService.chat(userInput);
        return new AiResponse(response, List.of(), Map.of("type", "chat"));
    }
}
