package pl.checkout.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddRequest {
    @NotBlank(message = "SKU must not be blank")
    private String sku;

    @Positive(message = "Quantity must be positive")
    private Integer quantity = 1;

    public String getSKU() {
        return sku;
    }
}
