package net.proselyte.eventapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.eventapp.dto.EventDto;
import net.proselyte.eventapp.dto.EventPageDto;
import net.proselyte.eventapp.dto.EventType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final Random random = new Random();

    private static final int TOTAL_EVENTS = 1000;

    public EventPageDto getEvents(int page, int size) {
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, TOTAL_EVENTS);
        int totalPages = (int) Math.ceil((double) TOTAL_EVENTS / size);

        var content = IntStream.range(fromIndex, toIndex)
                .mapToObj(this::generateEvent)
                .toList();

        return new EventPageDto(content, page, size, TOTAL_EVENTS, totalPages);
    }

    public EventDto save(EventDto dto) {
        EventDto eventDto = new EventDto(UUID.randomUUID().toString(), dto.type(), System.currentTimeMillis(), System.currentTimeMillis(), dto.description());

        String message = "event: " + eventDto.uid() + " received";

        Map<String, Object> notification = Map.of(
                "type", "SYSTEM_ALERT",
                "recipient", "admin@company.com",
                "message", message
        );

        return eventDto;
    }

    private EventDto generateEvent(int index) {
        return new EventDto(UUID.randomUUID().toString(), randomEventType(), System.currentTimeMillis() - random.nextInt(1_000_000), System.currentTimeMillis(), "Событие №" + index);
    }

    private EventType randomEventType() {
        EventType[] types = EventType.values();
        return types[random.nextInt(types.length)];
    }
}
