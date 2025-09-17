package pl.checkout.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.checkout.dto.request.ProductAddRequest;
import pl.checkout.dto.response.CheckoutSessionResponse;
import pl.checkout.exception.SessionNotFoundException;
import pl.checkout.model.Product;
import pl.checkout.repository.ProductRepository;
import pl.checkout.repository.CheckoutRepository;

import java.math.BigDecimal;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
public class CheckoutServiceTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setup() {
        checkoutRepository.deleteAll();
        productRepository.deleteAll();

        product1 = Product.builder()
                .sku("TEST001")
                .name("Product A")
                .price(BigDecimal.valueOf(40.00))
                .build();
        product2 = Product.builder()
                .sku("TEST002")
                .name("Product B")
                .price(BigDecimal.valueOf(60.00))
                .build();

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    public void should_create_new_session() {
        // When
        CheckoutSessionResponse session = checkoutService.createNewSession();

        // Then
        assertNotNull(session);
        assertNotNull(session.getId());
        assertTrue(session.getProducts().isEmpty());
    }

    @Test
    public void should_get_existing_session() {
        // Given
        CheckoutSessionResponse session = checkoutService.createNewSession();

        // When
        CheckoutSessionResponse fetchedSession = checkoutService.getSession(session.getId());

        // Then
        assertNotNull(fetchedSession);
        assertEquals(session.getId(), fetchedSession.getId());
    }

    @Test
    public void should_throw_exception_for_nonexistent_session() {
        // When & Then
        assertThrows(SessionNotFoundException.class, () -> {
            checkoutService.getSession(java.util.UUID.randomUUID());
        });
    }

    @Test
    public void should_add_products_to_new_session() {
        // Given
        CheckoutSessionResponse session = checkoutService.createNewSession();

        // When
        checkoutService.addProduct(session.getId(), new ProductAddRequest("TEST001", 2));

        // Then
        CheckoutSessionResponse updatedSession = checkoutService.getSession(session.getId());
        assertEquals(1, updatedSession.getProducts().size());
        assertEquals(2, updatedSession.getProducts().getFirst().getQuantity());
    }
}
