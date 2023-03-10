package za.co.tickItup.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.tickItup.api.entity.TicketPurchaseHistory;
import za.co.tickItup.api.request.CartRequest;
import za.co.tickItup.api.service.TicketPurchaseHistoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket-purchases")
public class TicketPurchaseController {

    @Autowired
    private TicketPurchaseHistoryService ticketPurchaseHistoryService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get ticket purchase history for user", description = "Retrieve a list of ticket purchases for a given user ID")
    @ApiResponse(responseCode = "200", description = "List of ticket purchases returned successfully", content = @Content(schema = @Schema(implementation = TicketPurchaseHistory.class)))
    public ResponseEntity<Object> getPurchaseHistoryForUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<TicketPurchaseHistory> ticketPurchases = ticketPurchaseHistoryService.getPurchaseHistoryForUser(userId);
        return new ResponseEntity<>(ticketPurchases, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{eventId}")
    @Operation(summary = "Get ticket purchase history for user and event", description = "Retrieve the ticket purchase for a given user ID and event ID")
    @ApiResponse(responseCode = "200", description = "Ticket purchase returned successfully", content = @Content(schema = @Schema(implementation = TicketPurchaseHistory.class)))
    public ResponseEntity<TicketPurchaseHistory> getTicketPurchaseHistory(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Event ID") @PathVariable Long eventId) {
        TicketPurchaseHistory ticketPurchase = ticketPurchaseHistoryService.getTicketPurchaseHistory(userId, eventId);
        return new ResponseEntity<>(ticketPurchase, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Purchase a ticket", description = "Purchase a ticket for a given user ID and event ID")
    @ApiResponse(responseCode = "200", description = "Ticket purchase successful", content = @Content(schema = @Schema(implementation = TicketPurchaseHistory.class)))
    public ResponseEntity<TicketPurchaseHistory> purchaseTicket(@Valid @RequestBody CartRequest cartRequest) {
        TicketPurchaseHistory ticketPurchase = ticketPurchaseHistoryService.purchaseTicket(cartRequest);
        return new ResponseEntity<>(ticketPurchase, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{eventId}")
    @Operation(summary = "Request a refund", description = "Request a refund for a given user ID and event ID")
    @ApiResponse(responseCode = "200", description = "Ticket refund requested", content = @Content(schema = @Schema(implementation = TicketPurchaseHistory.class)))
    public ResponseEntity<TicketPurchaseHistory> requestRefund(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Event ID") @PathVariable Long eventId) {
        TicketPurchaseHistory ticketPurchase = ticketPurchaseHistoryService.requestRefund(userId, eventId);
        return new ResponseEntity<>(ticketPurchase, HttpStatus.OK);
    }
}
