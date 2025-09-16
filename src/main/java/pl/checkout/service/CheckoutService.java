package pl.checkout.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.checkout.dto.request.CheckoutPaymentRequest;
import pl.checkout.dto.request.ProductAddRequest;
import pl.checkout.dto.request.ProductRemoveRequest;
import pl.checkout.dto.response.CheckoutReceiptResponse;
import pl.checkout.dto.response.CheckoutSessionResponse;
import pl.checkout.exception.SessionException;
import pl.checkout.exception.SessionNotFoundException;
import pl.checkout.model.CheckoutProduct;
import pl.checkout.model.CheckoutSession;
import pl.checkout.model.Product;
import pl.checkout.repository.CheckoutRepository;
import pl.checkout.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutService {

    private final PricingService pricingService;
    private final ProductRepository productRepository;
    private final CheckoutRepository checkoutRepository;

    public CheckoutSessionResponse createNewSession() {
        CheckoutSession session = CheckoutSession
                .builder()
                .build();

        session = checkoutRepository.save(session);

        return checkoutSessionBuilder(session);
    }

    public CheckoutSessionResponse getSession(UUID sessionId) {
        CheckoutSession session = getSessionOrThrow(sessionId);
        return checkoutSessionBuilder(session);
    }

    public CheckoutSessionResponse addProduct(UUID sessionId, ProductAddRequest request) {
        CheckoutSession session = getSessionOrThrow(sessionId);
        Product product = getProductOrThrow(request.getSKU());

        Optional<CheckoutProduct> checkoutProduct = session.getProducts().stream()
                .filter(cp -> cp.getProduct().getSKU().equals(request.getSKU()))
                .findFirst();

        if (checkoutProduct.isPresent()) {
            CheckoutProduct cp = checkoutProduct.get();
            cp.setQuantity(cp.getQuantity() + request.getQuantity());
        } else {
            CheckoutProduct newCheckoutProduct = CheckoutProduct
                    .builder()
                    .checkoutSession(session)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            session.getProducts().add(newCheckoutProduct);
        }

        pricingService.applyPricing(session);

        session = checkoutRepository.save(session);

        return checkoutSessionBuilder(session);
    }

    public CheckoutSessionResponse removeProduct(UUID sessionId, ProductRemoveRequest request) {
        CheckoutSession session = getSessionOrThrow(sessionId);

        Optional<CheckoutProduct> checkoutProduct = session.getProducts().stream()
                .filter(cp -> cp.getProduct().getSKU().equals(request.getSKU()))
                .findFirst();

        if (checkoutProduct.isPresent()) {
            CheckoutProduct cp = checkoutProduct.get();
            cp.setQuantity(cp.getQuantity() - 1);

            if (cp.getQuantity() <= 0 || request.isRemoveAll()) {
                session.getProducts().removeIf(product -> product.getId().equals(cp.getId()));
            }
        } else {
            throw new SessionException("Product with SKU " + request.getSKU() + " is not in the checkout session");
        }

        pricingService.applyPricing(session);

        session = checkoutRepository.save(session);

        return checkoutSessionBuilder(session);
    }

    public CheckoutReceiptResponse processPayment(UUID sessionId, CheckoutPaymentRequest request) {
        CheckoutSession session = getSessionOrThrow(sessionId);

        session.setPaymentStatus(request.getPaymentStatus());

        session = checkoutRepository.save(session);

        return checkoutReceiptBuilder(session);
    }

    private CheckoutSession getSessionOrThrow(UUID sessionId) {
        return checkoutRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Checkout session with ID " + sessionId + " does not exist"));
    }

    private Product getProductOrThrow(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product with SKU " + sku + " does not exist"));
    }

    private CheckoutSessionResponse checkoutSessionBuilder(CheckoutSession session) {
        List<CheckoutSessionResponse.SessionProductResponse> products = session.getProducts().stream()
                .map(checkoutProduct -> {
                    Product product = checkoutProduct.getProduct();
                    return new CheckoutSessionResponse.SessionProductResponse(
                            product.getSKU(),
                            product.getName(),
                            checkoutProduct.getQuantity(),
                            checkoutProduct.getPrice()
                    );
                }).toList();

        return CheckoutSessionResponse
                .builder()
                .id(session.getId())
                .products(products)
                .totalAmount(session.getTotalAmount())
                .totalDiscount(session.getTotalDiscount())
                .finalAmount(session.getFinalAmount())
                .build();
    }

    private CheckoutReceiptResponse checkoutReceiptBuilder(CheckoutSession session) {
        List<CheckoutReceiptResponse.ReceiptProductResponse> products = session.getProducts().stream()
                .map(checkoutProduct -> {
                    Product product = checkoutProduct.getProduct();
                    return new CheckoutReceiptResponse.ReceiptProductResponse(
                            product.getSKU(),
                            product.getName(),
                            checkoutProduct.getQuantity(),
                            checkoutProduct.getPrice(),
                            checkoutProduct.getDiscount(),
                            checkoutProduct.getFinalPrice()
                    );
                }).toList();

        return CheckoutReceiptResponse
                .builder()
                .id(session.getId())
                .paymentStatus(session.getPaymentStatus())
                .products(products)
                .totalAmount(session.getTotalAmount())
                .totalDiscount(session.getTotalDiscount())
                .finalAmount(session.getFinalAmount())
                .build();
    }
}
