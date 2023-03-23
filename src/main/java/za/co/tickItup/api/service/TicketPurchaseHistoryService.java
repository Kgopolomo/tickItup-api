package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.*;
import za.co.tickItup.api.repository.*;
import za.co.tickItup.api.request.CartRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Autowired private PaymentOptionRepository paymentOptionRepository;

    @Autowired private PaymentOptionService paymentOptionService;


    public List<TicketPurchaseHistory> getPurchaseHistoryForUser(Long userId) {
        UserProfile user = userService.getProfile(userId);
        List<TicketPurchaseHistory> byUserProfile = ticketPurchaseHistoryRepository.findByUserProfile(user);
        return byUserProfile;
    }

    public TicketPurchaseHistory getTicketPurchaseHistory(Long userId, Long eventId) {
        UserProfile user = userService.getProfile(userId);
        Event event = eventService.getEventById(eventId);
        return ticketPurchaseHistoryRepository.findByUserProfileAndEvent(user, event);
    }

    public TicketPurchaseHistory purchaseTicket(CartRequest cartRequest) {
        UserProfile user = userService.getProfile(cartRequest.getUserId());
        Event event = eventService.getEventById(cartRequest.getEventId());
        Ticket ticketType = ticketRepository.findByCode(cartRequest.getTicketCode()).get();
        PaymentOption paymentOption =  paymentOptionRepository.findByUserProfile(user);

        // Decrypt card number and cvv
        paymentOption.setCvv(paymentOptionService.decrypt(paymentOption.getCvv()));
        paymentOption.setCardNumber(paymentOptionService.decrypt(paymentOption.getCardNumber()));

        // Get ticket type from event tickets
        TicketType requestedTicketType = null;
        for (Ticket ticketTypeObj : event.getTickets()) {
            if (ticketTypeObj.getCode().equals(cartRequest.getTicketCode())) {
                requestedTicketType = ticketTypeObj.getTicketType();
                break;
            }
        }

        // Check if ticket type exists
        if (requestedTicketType == null) {
            throw new IllegalArgumentException("Invalid ticket type");
        }

        // Check if requested quantity is available
        int availableQuantity = requestedTicketType.getQuantity();
        if (cartRequest.getQuantity() > availableQuantity) {
            throw new IllegalArgumentException("Requested quantity not available");
        }

        // Deduct requested quantity from ticket type's available quantity
        requestedTicketType.setQuantity(availableQuantity - cartRequest.getQuantity());
        ticketTypeRepository.save(requestedTicketType);

        // Update ticket status to "Sold Out" if all tickets of a particular type are sold
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
            ticketPurchaseHistory.setTicket(ticketType);
            ticketPurchaseHistory.setTicketStatus(String.valueOf(TicketStatus.ACTIVE));

            return ticketPurchaseHistoryRepository.save(ticketPurchaseHistory);

    }

    public TicketPurchaseHistory requestRefund(Long userId, Long eventId) {
        TicketPurchaseHistory ticketPurchaseHistory = getTicketPurchaseHistory(userId, eventId);
        ticketPurchaseHistory.setTicketStatus(String.valueOf(TicketStatus.REFUND_REQUESTED));
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
