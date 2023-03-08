package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.Event;
import za.co.tickItup.api.entity.Ticket;
import za.co.tickItup.api.entity.TicketType;
import za.co.tickItup.api.repository.EventRepository;
import za.co.tickItup.api.repository.TicketRepository;
import za.co.tickItup.api.repository.TicketTypeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TicketService {

    @Autowired private EventRepository eventRepository;

    @Autowired private TicketTypeRepository ticketTypeRepository;

    @Autowired private TicketRepository ticketRepository;

    public List<Ticket> createTickets(List<Ticket> tickets, Event event) {
        List<Ticket> createdTickets = new ArrayList<>();
        for (Ticket ticket : tickets) {

            TicketType ticketType = new TicketType();
            ticket.setEvent(event);
            ticket.setCode(generateTicketCode());
            ticket.setTicketStatus("CREATED");
            ticketType.setQuantity(ticket.getTicketType().getQuantity() - 1);
            ticketType.setPrice(ticket.getTicketType().getPrice());
            ticketType.setName(ticket.getTicketType().getName());
            ticket.setTicketType(ticketType);
            ticketTypeRepository.save(ticketType);
            createdTickets.add(ticketRepository.save(ticket));
        }
        return createdTickets;
    }


    public Ticket getTicketByCode(String code) {
        return ticketRepository.findByCode(code).orElseThrow(() -> new NoSuchElementException("Ticket not found with code " + code));
    }

    public List<Ticket> getAllTicketsByEvent(Event event) {
        return ticketRepository.findAllByEvent(event);
    }

    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void deleteTicket(Ticket ticket) {
        ticketRepository.delete(ticket);
    }

}
