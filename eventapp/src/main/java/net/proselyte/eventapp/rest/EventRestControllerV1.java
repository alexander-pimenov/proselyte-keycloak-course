package net.proselyte.eventapp.rest;

import lombok.RequiredArgsConstructor;
import net.proselyte.eventapp.dto.EventDto;
import net.proselyte.eventapp.dto.EventPageDto;
import net.proselyte.eventapp.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventRestControllerV1 {

    private final EventService eventService;


    @GetMapping
    public EventPageDto getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getEvents(page, size);
    }

    @PostMapping
    public EventDto createEvent(@RequestBody EventDto dto) {
        return eventService.save(dto);
    }
}
