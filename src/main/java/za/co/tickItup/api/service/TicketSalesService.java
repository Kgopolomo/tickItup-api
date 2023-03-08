package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.Event;
import za.co.tickItup.api.entity.Ticket;
import za.co.tickItup.api.repository.TicketRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketSalesService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventService eventService;

    public int getTicketSales(Long eventId, String ticketType) {
        Event event = eventService.getEventById(eventId);

        // Get the list of tickets for the event and ticket type
        List<Ticket> tickets = event.getTickets().stream()
                .filter(ticket -> ticket.getTicketType().getName().equals(ticketType))
                .collect(Collectors.toList());

        // Calculate the number of sold tickets
        int soldTickets = (int) tickets.stream()
                .filter(ticket -> ticket.getTicketStatus().equals("SOLD"))
                .count();

        return soldTickets;
    }
//
//    public int getTotalTicketSales(Long eventId) {
//        Event event = eventService.getEventById(eventId);
//
//        // Calculate the total number of sold tickets for the event
//        int totalSoldTickets = event.getTickets().stream()
//                .filter(ticket -> ticket.getTicketStatus().equals("SOLD"))
//                .mapToInt(Ticket::getQuantity)
//                .sum();
//
//        return totalSoldTickets;
//    }

//    public int getAvailableTicketQuantity(Long eventId, String ticketType) {
//        Event event = eventService.getEventById(eventId);
//
//        // Get the ticket type object for the specified ticket type
//        TicketType requestedTicketType = null;
//        for (Ticket ticketTypeObj : event.getTickets()) {
//            if (ticketTypeObj.getTicketType().getName().equals(ticketType)) {
//                requestedTicketType = ticketTypeObj.getTicketType();
//                break;
//            }
//        }
//
//        if (requestedTicketType == null) {
//            throw new IllegalArgumentException("Invalid ticket type");
//        }
//
//        int availableQuantity = requestedTicketType.getQuantity();
//
//        // Calculate the number of sold tickets for the specified ticket type
//        int soldTickets = (int) event.getTickets().stream()
//                .filter(ticket -> ticket.getTicketType().getName().equals(ticketType) && ticket.getTicketStatus().equals("SOLD"))
//                .mapToInt(Ticket::getQuantity)
//                .sum();
//
//        int availableTicketQuantity = availableQuantity - soldTickets;
//
//        return availableTicketQuantity;
//    }
}
