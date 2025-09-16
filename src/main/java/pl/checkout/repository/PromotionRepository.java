package pl.checkout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.checkout.model.Promotion;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
    @Query("SELECT p FROM Promotion p " +
            "JOIN FETCH p.products pp " +
            "JOIN FETCH pp.product " +
            "WHERE p.id IN (SELECT DISTINCT p2.id FROM Promotion p2 " +
            "JOIN p2.products pp2 " +
            "WHERE pp2.product.id IN :productIds)")
    List<Promotion> findByProductIds(@Param("productIds") Set<UUID> productIds);
}