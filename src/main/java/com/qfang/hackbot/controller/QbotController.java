package com.qfang.hackbot.controller;

import com.qfang.hackbot.service.QbotService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for QBot functionality.
 * Serves the main page and handles question answering.
 */
@Controller
public class QbotController {

    private final QbotService qbotService;

    public QbotController(QbotService qbotService) {
        this.qbotService = qbotService;
    }

    /**
     * Serve the main page.
     */
    @GetMapping("/")
    public String index() {
        return "qbot.html";
    }

    /**
     * API endpoint to get answers to questions.
     * Responses are cached for performance.
     */
    @GetMapping("/api/answer")
    @ResponseBody
    public String getAnswer(@RequestParam String question) {
        return qbotService.findAnswer(question);
    }
}
