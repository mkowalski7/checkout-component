package pl.checkout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.checkout.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Table(name = "checkout_sessions")
@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CheckoutSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @OneToMany(mappedBy = "checkoutSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CheckoutProduct> products = new LinkedHashSet<>();

    @Column
    @PositiveOrZero
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column
    @PositiveOrZero
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column
    @PositiveOrZero
    @Builder.Default
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
