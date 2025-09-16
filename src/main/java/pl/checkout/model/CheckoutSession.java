package pl.checkout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.checkout.enums.CheckoutStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private CheckoutStatus status;

    @OneToMany(mappedBy = "checkoutSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CheckoutProduct> products;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
