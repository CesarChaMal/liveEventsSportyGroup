package com.sportygroup.liveevents.infrastructure.adapter;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventTrackingService {

    private final Set<String> liveEvents = ConcurrentHashMap.newKeySet();

    public void updateEventStatus(String eventId, String status) {
        if ("live".equalsIgnoreCase(status)) {
            liveEvents.add(eventId);
        } else {
            liveEvents.remove(eventId);
        }
    }

    public Set<String> getLiveEventIds() {
        return Collections.unmodifiableSet(liveEvents);
    }
}
