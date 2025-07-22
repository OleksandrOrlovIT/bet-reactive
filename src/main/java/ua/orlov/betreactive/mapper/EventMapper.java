package ua.orlov.betreactive.mapper;

import org.springframework.stereotype.Component;
import ua.orlov.betreactive.dto.CreateEventRequest;
import ua.orlov.betreactive.dto.UpdateEventRequest;
import ua.orlov.betreactive.model.Event;

@Component
public class EventMapper {

    public Event mapCreateEventRequestToEvent(CreateEventRequest createEventRequest) {
        return Event.builder()
                .name(createEventRequest.getName())
                .startDate(createEventRequest.getStartDate())
                .endDate(createEventRequest.getEndDate())
                .build();
    }

    public Event mapUpdateEventRequestToEvent(UpdateEventRequest updateEventRequest) {
        return Event.builder()
                .id(updateEventRequest.getId())
                .name(updateEventRequest.getName())
                .startDate(updateEventRequest.getStartDate())
                .endDate(updateEventRequest.getEndDate())
                .build();
    }
}
