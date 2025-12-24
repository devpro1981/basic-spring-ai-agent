package com.example.poc.basagent;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentController {

    private final RagService ragService;

    public AgentController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping(path = "/docs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RagService.UpsertResult upsert(@RequestBody UpsertRequest request) {
        return ragService.upsertDocument(request.id(), request.text());
    }

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RagService.ChatResult chat(@RequestBody ChatRequest request) {
        return ragService.chat(request.message());
    }

    public record UpsertRequest(String id, String text) {}
    public record ChatRequest(String message) {}
}

