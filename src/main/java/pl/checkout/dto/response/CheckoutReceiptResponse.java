package pl.checkout.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.checkout.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckoutReceiptResponse {
    private UUID id;

    private PaymentStatus paymentStatus;
    private List<ReceiptProductResponse> products;

    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private BigDecimal finalAmount;

    @Data
    @AllArgsConstructor
    public static class ReceiptProductResponse {
        private String sku;
        private String name;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal discount;
        private BigDecimal finalPrice;
    }
}
