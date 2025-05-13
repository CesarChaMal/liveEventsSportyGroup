package com.sportygroup.liveevents.controller;

import com.sportygroup.liveevents.model.EventStatusRequest;
import com.sportygroup.liveevents.service.EventSchedulerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStatusControllerTest {

    @Mock
    EventSchedulerService schedulerService;

    @InjectMocks
    EventStatusController controller;

    @Test
    void updateEventStatus_shouldReturnOk() {
        EventStatusRequest request = new EventStatusRequest("match-1", "live");
        ResponseEntity<String> response = controller.updateEventStatus(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event status updated.", response.getBody());
        verify(schedulerService).updateEventStatus("match-1", "live");
    }
}
