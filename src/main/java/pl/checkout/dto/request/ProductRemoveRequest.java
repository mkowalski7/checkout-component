package pl.checkout.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRemoveRequest {
    @NotBlank(message = "SKU must not be blank")
    private String sku;

    private boolean removeAll = false;

    public String getSKU() {
        return sku;
    }
}
