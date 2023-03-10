package za.co.tickItup.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartRequest {

    private Long userId;
    private Long eventId;
    private String ticketCode;
    private int quantity;
    private Long paymentOptionId;
}
