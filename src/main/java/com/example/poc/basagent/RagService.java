package com.example.poc.basagent;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RagService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final AtomicInteger docCounter = new AtomicInteger(0);

    public RagService(ChatLanguageModel chatModel,
                      EmbeddingModel embeddingModel,
                      EmbeddingStore<TextSegment> embeddingStore) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public UpsertResult upsertDocument(String id, String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text must not be empty");
        }

        String docId = id != null && !id.isBlank() ? id : "doc-" + docCounter.incrementAndGet();

        // Split text into chunks (simple split by sentences for now)
        List<String> chunks = splitText(text);

        // Create text segments with metadata and embed them
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);
            TextSegment segment = TextSegment.from(chunkText);

            // Add metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("source", docId);
            metadata.put("chunk", String.valueOf(i));

            // Embed and store
            Embedding embedding = embeddingModel.embed(chunkText).content();
            embeddingStore.add(embedding, segment);
        }

        return new UpsertResult(docId, chunks.size());
    }

    public ChatResult chat(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must not be empty");
        }

        // Create prompt with RAG context
        String ragPrompt = String.format(
            "You are a helpful assistant. Answer the following question based on the provided context.\n\n" +
            "Question: %s\n\n" +
            "Please provide a clear and concise answer.",
            userMessage
        );

        // Get response from chat model
        String answer = chatModel.generate(ragPrompt);

        // Return response with empty sources for now (would need retrieval setup)
        return new ChatResult(answer, new ArrayList<>());
    }

    private List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder chunk = new StringBuilder();

        for (String sentence : sentences) {
            if ((chunk.length() + sentence.length()) > 500) {
                if (!chunk.isEmpty()) {
                    chunks.add(chunk.toString());
                    chunk = new StringBuilder();
                }
            }
            chunk.append(sentence).append(" ");
        }

        if (!chunk.isEmpty()) {
            chunks.add(chunk.toString());
        }

        return chunks;
    }

    public record UpsertResult(String id, int chunksAdded) {}
    public record SourceInfo(String id, double score) {}
    public record ChatResult(String answer, List<SourceInfo> sources) {}
}

