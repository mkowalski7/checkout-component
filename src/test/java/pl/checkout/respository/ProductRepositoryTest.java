package pl.checkout.respository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.checkout.model.Product;
import pl.checkout.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void should_find_products() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    public void should_find_no_products() {
        // Given
        productRepository.deleteAll();

        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    public void should_store_a_product() {
        // Given
        Product product = Product.builder()
                .sku("SKU007")
                .name("Product X")
                .price(BigDecimal.valueOf(100.00))
                .build();

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals("SKU007", savedProduct.getSKU());
        assertEquals("Product X", savedProduct.getName());
        assertEquals(BigDecimal.valueOf(100.00), savedProduct.getPrice());
    }

    @Test
    public void should_find_product_by_sku() {
        // Given
        Product product = Product.builder()
                .sku("SKU007")
                .name("Product X")
                .price(BigDecimal.valueOf(100.00))
                .build();
        entityManager.persistAndFlush(product);

        // When
        Optional<Product> foundProduct = productRepository.findBySku("SKU007");

        // Then
        assertNotNull(foundProduct);
        assertTrue(foundProduct.isPresent());
        assertEquals("SKU007", foundProduct.get().getSKU());
        assertEquals("Product X", foundProduct.get().getName());
        assertEquals(BigDecimal.valueOf(100.00), foundProduct.get().getPrice());
    }

    @Test
    public void should_not_find_product_by_sku_when_sku_does_not_exist() {
        // When
        Optional<Product> foundProduct = productRepository.findBySku("NON_EXISTENT_SKU");

        // Then
        assertNotNull(foundProduct);
        assertFalse(foundProduct.isPresent());
    }
}
