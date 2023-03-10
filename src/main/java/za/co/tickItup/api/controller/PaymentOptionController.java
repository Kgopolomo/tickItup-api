package za.co.tickItup.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.tickItup.api.entity.PaymentOption;
import za.co.tickItup.api.entity.TicketPurchaseHistory;
import za.co.tickItup.api.request.CartRequest;
import za.co.tickItup.api.service.PaymentOptionService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/payment-options")
public class PaymentOptionController {

    @Autowired
    PaymentOptionService paymentOptionService;

    @PostMapping
    @Operation(summary = "Add a Payment Option", description = "Purchase a ticket for a given user ID and event ID")
    @ApiResponse(responseCode = "200", description = "Ticket purchase successful", content = @Content(schema = @Schema(implementation = TicketPurchaseHistory.class)))
    public ResponseEntity<PaymentOption> addPaymentOption(@Valid @RequestBody PaymentOption paymentOption) {
        PaymentOption card = paymentOptionService.addPaymentOption(paymentOption);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }
}
