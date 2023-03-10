package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.Event;
import za.co.tickItup.api.entity.Ticket;
import za.co.tickItup.api.repository.EventDetailRepository;
import za.co.tickItup.api.repository.EventRepository;
import za.co.tickItup.api.repository.LocationRepository;
import za.co.tickItup.api.repository.TicketTypeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

     @Autowired private  EventRepository eventRepository;

     @Autowired private TicketTypeRepository ticketTypeRepository;

     @Autowired private TicketService ticketService;

     @Autowired private LocationRepository locationRepository;

     @Autowired private EventDetailRepository eventDetailRepository;

    public Event createEvent(Event event) {

        // Check if event with same name and date-time already exists
        List<Event> events = eventRepository.findByNameAndStartDateTime(event.getName(), event.getStartDateTime());
        if (!events.isEmpty()) {
            throw new IllegalArgumentException("Event with same name and start date-time already exists");
        }

        int ticketCount = 0;
        for (Ticket ticket : event.getTickets()) {
            ticketCount += ticket.getTicketType().getQuantity();
        }

        if (ticketCount >= event.getCapacity()) {
            throw new IllegalArgumentException("Tickets are more then event capacity");
        }

        Event savedEvent = eventRepository.save(event);
        for (Ticket ticket : event.getTickets()) {
            ticket.setCode(generateTicketCode());
            ticket.setTicketStatus("CREATED");
            ticket.setEvent(savedEvent);
            ticket.setTicketType(ticketTypeRepository.save(ticket.getTicketType()));

        }
        locationRepository.save(event.getLocation());
        eventDetailRepository.save(savedEvent.getEventDetail());

        return savedEvent;
    }

    public Event updateEvent(Event event) {
        Event existingEvent = getEventById(event.getId());
        existingEvent.setName(event.getName());
        existingEvent.setStartDateTime(event.getStartDateTime());
        existingEvent.setEndDateTime(event.getEndDateTime());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setImage(event.getImage());
        return eventRepository.save(existingEvent);
    }

    public Event getEventById(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isPresent()) {
            throw new EntityNotFoundException("Event not found");
        }
        return optionalEvent.get();
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
