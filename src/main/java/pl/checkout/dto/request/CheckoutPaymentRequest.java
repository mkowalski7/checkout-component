package pl.checkout.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.checkout.enums.PaymentStatus;

@Data
public class CheckoutPaymentRequest {
    @NotNull
    private PaymentStatus paymentStatus;
}
