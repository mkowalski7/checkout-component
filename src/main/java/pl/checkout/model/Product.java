package pl.checkout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "products")
@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 20)
    @NotEmpty
    private String sku;

    @Column
    @NotEmpty
    private String name;

    @Column(precision = 10, scale = 2)
    @Positive
    private BigDecimal price;

    public String getSKU() {
        return sku;
    }
}
