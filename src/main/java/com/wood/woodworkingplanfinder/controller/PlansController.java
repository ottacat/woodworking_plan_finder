package com.wood.woodworkingplanfinder.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plans")
@AllArgsConstructor
public class PlansController {
    private final OpenAiChatClient chatClient;

    @GetMapping("/find/{item}")
    public String findPlan(@PathVariable("item") String item) {
        String prompt =
                "I'd like a woodworking plan for a {item}";
        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        promptTemplate.add("item", item);
        String chatResponse = chatClient.call(promptTemplate.render());
        return chatResponse;
    }
}
