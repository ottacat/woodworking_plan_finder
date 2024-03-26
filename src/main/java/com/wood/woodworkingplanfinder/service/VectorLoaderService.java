package com.wood.woodworkingplanfinder.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorLoaderService {
    @Value("classpath:transcripts/apple_press.txt")
    private Resource resource;
    private final VectorStore vectorStore;

    public VectorLoaderService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void loadDocuments() {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("author", "Bourbon Moth");
        textReader.getCustomMetadata().put("video_url", "https://www.youtube.com/watch?v=KEtTen0LHNo&t=4s");
        List<Document> res = textReader.get();

        vectorStore.accept(res);
    }
}
