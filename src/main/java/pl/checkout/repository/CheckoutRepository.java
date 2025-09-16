package pl.checkout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.checkout.enums.PaymentStatus;
import pl.checkout.model.CheckoutSession;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutSession, UUID> {
    Optional<CheckoutSession> findByIdAndStatus(UUID id, PaymentStatus status);
}
