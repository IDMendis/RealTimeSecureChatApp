package com.example.chatserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic Spring Boot application context test.
 * 
 * This test ensures that the Spring application context loads successfully
 * with all beans configured correctly.
 */
@SpringBootTest
class ChatServerApplicationTests {

    /**
     * Test that the application context loads without errors.
     * This verifies:
     * - All beans are created successfully
     * - No circular dependencies
     * - Configuration is valid
     */
    @Test
    void contextLoads() {
        // If the context loads successfully, this test passes
    }
}
