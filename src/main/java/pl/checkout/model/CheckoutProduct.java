package pl.checkout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "checkout_products")
@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CheckoutProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "checkout_session_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private CheckoutSession checkoutSession;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Product product;

    @Column
    @Positive
    private Integer quantity;

    @Column(precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal discount;
}
