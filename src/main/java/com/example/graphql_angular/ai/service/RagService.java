package com.example.graphql_angular.ai.service;

import com.example.graphql_angular.ai.model.AiResponse;
import com.example.graphql_angular.ai.model.DocumentRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final VectorStoreService vectorStoreService;
    private final LlmService llmService;
    private final CourseServiceClient courseClient;

    public AiResponse ask(String question) {
        List<DocumentRecord> relevantDocs = vectorStoreService.search(question, 3);

        String docContext = relevantDocs.stream()
                .map(d -> "Title: " + d.getTitle() + "\nContent: " + d.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        List<Map<String, Object>> courses = courseClient.getAllCourses();
        String courseContext = courses.stream()
                .map(c -> "Course: " + c.get("title") + " - " + c.get("description") + " (Instructor: " + c.get("instructor") + ", Credits: " + c.get("credits") + ")")
                .collect(Collectors.joining("\n"));

        StringBuilder fullContext = new StringBuilder();
        if (!docContext.isEmpty()) {
            fullContext.append("=== Documents ===\n").append(docContext);
        }
        if (!courseContext.isEmpty()) {
            if (fullContext.length() > 0) fullContext.append("\n\n");
            fullContext.append("=== Available Courses ===\n").append(courseContext);
        }
        if (fullContext.isEmpty()) {
            fullContext.append("No relevant context found.");
        }

        List<String> sources = relevantDocs.stream()
                .map(DocumentRecord::getTitle)
                .collect(Collectors.toList());
        if (!courses.isEmpty()) sources.add("Course Catalog (" + courses.size() + " courses)");

        String systemPrompt = "You are a helpful AI assistant. Answer the question based on the provided context from documents and course catalog. "
                + "If the context doesn't contain enough information, say so honestly."
                + "\n\nContext:\n" + fullContext;

        String response = llmService.chatWithContext(systemPrompt, question);

        return new AiResponse(
                response,
                sources,
                Map.of("context_chunks", relevantDocs.size(), "courses_available", courses.size(), "model", "llama3.2")
        );
    }
}
