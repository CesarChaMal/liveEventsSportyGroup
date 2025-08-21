package com.sportygroup.liveevents.in.web;

import com.sportygroup.liveevents.application.dto.EventStatusRequest;
import com.sportygroup.liveevents.application.usecase.UpdateEventStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class EventStatusControllerTest {

    private EventStatusController controller;

    @Mock
    private UpdateEventStatusUseCase updateEventStatusUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new EventStatusController(updateEventStatusUseCase);
    }

    @Test
    void updateEventStatus_shouldReturnOk() {
        EventStatusRequest request = new EventStatusRequest("match-123", "live");

        ResponseEntity<String> response = controller.updateEventStatus(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event status updated.", response.getBody());

        verify(updateEventStatusUseCase).execute("match-123", "live");
    }

    @Test
    void updateEventStatus_shouldHandleNotLiveStatus() {
        EventStatusRequest request = new EventStatusRequest("match-123", "not live");

        ResponseEntity<String> response = controller.updateEventStatus(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event status updated.", response.getBody());

        verify(updateEventStatusUseCase).execute("match-123", "not live");
    }
}
