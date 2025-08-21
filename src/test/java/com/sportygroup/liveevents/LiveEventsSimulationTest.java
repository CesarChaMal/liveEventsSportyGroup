package com.sportygroup.liveevents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class LiveEventsSimulationTest {

    private LiveEventsSimulation simulation;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        simulation = new LiveEventsSimulation();
        
        // Capture console output for verification
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void shouldRunAllTestCasesSuccessfully() {
        // When
        simulation.runAllTestCases();
        
        // Then
        String output = outputStream.toString();
        
        // Verify all test cases ran
        assertTrue(output.contains("Test Case 1: Basic Event Lifecycle"));
        assertTrue(output.contains("Test Case 2: Multiple Live Events"));
        assertTrue(output.contains("Test Case 3: Event Status Changes"));
        assertTrue(output.contains("Test Case 4: Single Event Processing"));
        assertTrue(output.contains("Test Case 5: Error Handling"));
        
        // Verify successful completion
        assertTrue(output.contains("All test cases completed successfully"));
        
        // Verify events were published
        assertTrue(output.contains("Published: Event="));
        
        // Restore original output
        System.setOut(originalOut);
    }

    @Test
    void shouldHandleMainMethodExecution() {
        // When/Then - Should not throw any exceptions
        assertDoesNotThrow(() -> LiveEventsSimulation.main(new String[]{}));
        
        // Restore original output
        System.setOut(originalOut);
    }
}