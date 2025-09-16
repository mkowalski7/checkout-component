package pl.checkout.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductAddRequest {
    @NotBlank(message = "SKU must not be blank")
    private String sku;

    @Positive(message = "Quantity must be positive")
    private Integer quantity = 1;

    public String getSKU() {
        return sku;
    }
}
