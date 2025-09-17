package pl.checkout.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ProductTest {

    private Product product;

    @BeforeEach
    public void setup() {
        product = Product.builder()
                .sku("SKU001")
                .name("Product A")
                .price(BigDecimal.valueOf(40.00))
                .build();
    }

    @Test
    public void should_build_using_builder() {
        // Given & When
        Product testProduct = Product.builder()
                .sku("SKU001")
                .name("Product A")
                .price(BigDecimal.valueOf(40.00))
                .build();

        // Then
        assertNotNull(testProduct);
        assertEquals("SKU001", testProduct.getSKU());
        assertEquals("Product A", testProduct.getName());
        assertEquals(BigDecimal.valueOf(40.00), testProduct.getPrice());
    }

    @Test
    public void should_return_proper_sku() {
        // When
        String sku = product.getSKU();

        // Then
        assertEquals("SKU001", sku);
    }

    @Test
    public void should_return_proper_name() {
        // When
        String name = product.getName();

        // Then
        assertEquals("Product A", name);
    }

    @Test
    public void should_return_proper_price() {
        // When
        BigDecimal price = product.getPrice();

        // Then
        assertTrue(price.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.valueOf(40.00), price);
    }
}
