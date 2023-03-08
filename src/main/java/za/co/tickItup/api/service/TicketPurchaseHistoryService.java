package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.*;
import za.co.tickItup.api.repository.EventRepository;
import za.co.tickItup.api.repository.TicketPurchaseHistoryRepository;
import za.co.tickItup.api.repository.TicketRepository;
import za.co.tickItup.api.repository.TicketTypeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketPurchaseHistoryService {

    @Autowired
    private TicketPurchaseHistoryRepository ticketPurchaseHistoryRepository;

    @Autowired
    private UserProfileService userService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired private EventService eventService;

    @Autowired private TicketTypeRepository ticketTypeRepository;

    @Autowired private TicketRepository ticketRepository;


    public List<TicketPurchaseHistory> getPurchaseHistoryForUser(Long userId) {
        UserProfile user = userService.getProfile(userId);
        return ticketPurchaseHistoryRepository.findByUserProfile(user);
    }

    public TicketPurchaseHistory getTicketPurchaseHistory(Long userId, Long eventId) {
        UserProfile user = userService.getProfile(userId);
        Event event = eventService.getEventById(eventId);
        return ticketPurchaseHistoryRepository.findByUserProfileAndEvent(user, event);
    }

    public TicketPurchaseHistory purchaseTicket(Long userId, Long eventId, String ticketType, int quantity) {
        UserProfile user = userService.getProfile(userId);
        Event event = eventService.getEventById(eventId);

        TicketType requestedTicketType = null;
        for (Ticket ticketTypeObj : event.getTickets()) {
            if (ticketTypeObj.getTicketType().getName().equals(ticketType)) {
                requestedTicketType = ticketTypeObj.getTicketType();
                break;
            }
        }

        if (requestedTicketType == null) {
            throw new IllegalArgumentException("Invalid ticket type");
        }

        int availableQuantity = requestedTicketType.getQuantity();
        if (quantity > availableQuantity) {
            throw new IllegalArgumentException("Requested quantity not available");
        }

        requestedTicketType.setQuantity(availableQuantity - quantity);
        ticketTypeRepository.save(requestedTicketType);

        if (requestedTicketType.getQuantity() == 0) {
            TicketType finalRequestedTicketType = requestedTicketType;
            List<Ticket> tickets = event.getTickets().stream()
                    .filter(ticket -> ticket.getTicketType().getId().equals(finalRequestedTicketType.getId()))
                    .collect(Collectors.toList());

            tickets.forEach(ticket -> {
                ticket.setTicketStatus("Sold Out");
                ticketRepository.save(ticket);
            });
        }

        TicketPurchaseHistory ticketPurchaseHistory = new TicketPurchaseHistory();
        ticketPurchaseHistory.setUserProfile(user);
        ticketPurchaseHistory.setEvent(event);
        ticketPurchaseHistory.setQrcode(generateTicketCode());
        ticketPurchaseHistory.setPurchaseDate(LocalDateTime.now());
        ticketPurchaseHistory.setTicketType(ticketType);
        ticketPurchaseHistory.setTicketStatus("ACTIVE");

        return ticketPurchaseHistoryRepository.save(ticketPurchaseHistory);
    }

    public TicketPurchaseHistory requestRefund(Long userId, Long eventId) {
        TicketPurchaseHistory ticketPurchaseHistory = getTicketPurchaseHistory(userId, eventId);
        ticketPurchaseHistory.setTicketStatus("REFUND_REQUESTED");
        return ticketPurchaseHistoryRepository.save(ticketPurchaseHistory);
    }

    public TicketPurchaseHistory transferTicket(Long userId, Long eventId, String recipientEmail) {
        TicketPurchaseHistory ticketPurchaseHistory = getTicketPurchaseHistory(userId, eventId);
        UserProfile recipient = userService.getUserByEmail(recipientEmail);
        ticketPurchaseHistory.setUserProfile(recipient);
        return ticketPurchaseHistoryRepository.save(ticketPurchaseHistory);
    }

    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
