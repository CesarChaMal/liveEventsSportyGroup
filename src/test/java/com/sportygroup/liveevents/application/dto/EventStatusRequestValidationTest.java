package com.sportygroup.liveevents.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventStatusRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAcceptValidStatus() {
        EventStatusRequest request = new EventStatusRequest("match-1", "live");
        Set<ConstraintViolation<EventStatusRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no violations for valid input");
    }

    @Test
    void shouldRejectBlankEventId() {
        EventStatusRequest request = new EventStatusRequest(" ", "live");
        Set<ConstraintViolation<EventStatusRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for blank eventId");
    }

    @Test
    void shouldRejectInvalidStatus() {
        EventStatusRequest request = new EventStatusRequest("match-1", "delayed");
        Set<ConstraintViolation<EventStatusRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for invalid status");
    }
}
