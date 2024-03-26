package com.wood.woodworkingplanfinder.controller;

import com.wood.woodworkingplanfinder.service.VectorLoaderService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plans")

public class PlansController {
    @Value("classpath:prompts/summarize-prompt.st")
    private Resource systemPromptTemplate = null;

    private final OpenAiChatClient chatClient;
    private final VectorLoaderService vectorLoaderService;
    private final VectorStore vectorStore;

    public PlansController(OpenAiChatClient chatClient, VectorLoaderService vectorLoaderService, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorLoaderService = vectorLoaderService;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/search/{prompt}")
    public String search(@PathVariable("prompt") String prompt) {
        List<Document> documents = vectorStore.similaritySearch(prompt);

        UserMessage userMessage = new UserMessage((prompt));
        Message systemMessage = getSystemMessage(documents);

        Prompt promptToSend = new Prompt(List.of(userMessage, systemMessage));
        ChatResponse response = chatClient.call(promptToSend);
        return response.getResults().toString();
    }

    private Message getSystemMessage(List<Document> documents) {
        String similarDocuments = documents.stream()
                .map(entry -> entry.getContent()).collect(Collectors.joining("\n"));
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(systemPromptTemplate);
        return promptTemplate.createMessage(Map.of("documents", similarDocuments));
    }

    @GetMapping("/find/{item}")
    public String findPlan(@PathVariable("item") String item) {
        String prompt =
                "I'd like a woodworking plan for a {item}";
        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        promptTemplate.add("item", item);
        return chatClient.call(promptTemplate.render());
    }

    @GetMapping("/import")
    public String createPlan() {
        vectorLoaderService.loadDocuments();
        return "Documents loaded";
    }
}
