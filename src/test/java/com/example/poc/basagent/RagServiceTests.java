package com.example.poc.basagent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class RagServiceTests {

    @Test
    void upsertRejectsEmptyText() {
        RagService service = new RagService(
                Mockito.mock(ChatLanguageModel.class),
                Mockito.mock(EmbeddingModel.class),
                Mockito.mock(EmbeddingStore.class)
        );
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.upsertDocument("id", " "));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void chatRejectsEmptyMessage() {
        RagService service = new RagService(
                Mockito.mock(ChatLanguageModel.class),
                Mockito.mock(EmbeddingModel.class),
                Mockito.mock(EmbeddingStore.class)
        );
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.chat(""));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
