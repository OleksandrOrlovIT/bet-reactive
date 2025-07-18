package ua.orlov.betreactive.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.model.Event;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventMapperTest {

    @InjectMocks
    private EventMapper eventMapper;

    @Test
    void mapCreateEventRequestToEventThenSuccess() {
        CreateEventRequest createEventRequest = new CreateEventRequest();
        createEventRequest.setName("Event name");
        createEventRequest.setStartDate(LocalDateTime.now());
        createEventRequest.setEndDate(LocalDateTime.now());

        Event event = eventMapper.mapCreateEventRequestToEvent(createEventRequest);

        assertAll(
                () -> assertEquals(createEventRequest.getName(), event.getName()),
                () -> assertEquals(createEventRequest.getStartDate(), event.getStartDate()),
                () -> assertEquals(createEventRequest.getEndDate(), event.getEndDate())
        );
    }

    @Test
    void mapUpdateUserRequestToUserThenSuccess() {
        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setId(UUID.randomUUID());
        updateEventRequest.setName("Event name");
        updateEventRequest.setStartDate(LocalDateTime.now());
        updateEventRequest.setEndDate(LocalDateTime.now());

        Event event = eventMapper.mapUpdateEventRequestToEvent(updateEventRequest);

        assertAll(
                () -> assertEquals(updateEventRequest.getId(), event.getId()),
                () -> assertEquals(updateEventRequest.getName(), event.getName()),
                () -> assertEquals(updateEventRequest.getStartDate(), event.getStartDate()),
                () -> assertEquals(updateEventRequest.getEndDate(), event.getEndDate())
        );
    }

}
