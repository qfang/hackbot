package com.qfang.hackbot.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling QBot questions and answers.
 * Uses caching to improve performance for repeated questions.
 */
@Service
public class QbotService {

    private final Map<String, String> questions = new HashMap<>();

    public QbotService() {
        questions.put("what is your name", "my name is qbot");
        questions.put("what time is it", "$findTime");
    }

    /**
     * Find answer to a question.
     * Results are cached to improve response time for frequent questions.
     * Cache only applies to non-null questions.
     */
    @Cacheable(value = "answers", key = "#question", condition = "#question != null")
    public String findAnswer(String question) {
        if (question == null) {
            return "Please ask a question";
        }

        String answer = questions.get(question.toLowerCase());
        
        if (answer == null) {
            return "I don't know the answer to that question";
        }

        // Handle special commands
        if (answer.startsWith("$")) {
            String command = answer.substring(1);
            if ("findTime".equals(command)) {
                return findTime();
            }
        }

        return answer;
    }

    private String findTime() {
        return "right now it's " + LocalDateTime.now();
    }
}
