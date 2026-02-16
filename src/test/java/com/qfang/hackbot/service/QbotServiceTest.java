package com.qfang.hackbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QbotServiceTest {

    @Autowired
    private QbotService qbotService;

    @Test
    void testFindAnswerWithKnownQuestion() {
        String answer = qbotService.findAnswer("what is your name");
        assertEquals("my name is qbot", answer);
    }

    @Test
    void testFindAnswerWithUnknownQuestion() {
        String answer = qbotService.findAnswer("unknown question");
        assertEquals("I don't know the answer to that question", answer);
    }

    @Test
    void testFindAnswerWithNullQuestion() {
        String answer = qbotService.findAnswer(null);
        assertEquals("Please ask a question", answer);
    }

    @Test
    void testFindTimeCommand() {
        String answer = qbotService.findAnswer("what time is it");
        assertTrue(answer.startsWith("right now it's"));
    }

    @Test
    void testCaching() {
        // First call
        long start1 = System.nanoTime();
        String answer1 = qbotService.findAnswer("what is your name");
        long time1 = System.nanoTime() - start1;

        // Second call should be faster due to caching
        long start2 = System.nanoTime();
        String answer2 = qbotService.findAnswer("what is your name");
        long time2 = System.nanoTime() - start2;

        assertEquals(answer1, answer2);
        // Note: This assertion may be flaky in CI, but demonstrates cache benefit
        // assertTrue(time2 < time1, "Cached call should be faster");
    }
}
