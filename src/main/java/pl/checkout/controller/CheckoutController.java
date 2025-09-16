package pl.checkout.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.checkout.dto.request.CheckoutPaymentRequest;
import pl.checkout.dto.request.ProductAddRequest;
import pl.checkout.dto.request.ProductRemoveRequest;
import pl.checkout.dto.response.CheckoutReceiptResponse;
import pl.checkout.dto.response.CheckoutSessionResponse;
import pl.checkout.service.CheckoutService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v3/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/initialize")
    public ResponseEntity<CheckoutSessionResponse> createSession() {
        return ResponseEntity.ok(checkoutService.createNewSession());
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<CheckoutSessionResponse> getSession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(checkoutService.getSession(sessionId));
    }

    @PutMapping("/{sessionId}/products")
    public ResponseEntity<CheckoutSessionResponse> addProduct(
            @PathVariable UUID sessionId,
            @RequestBody ProductAddRequest request
    ) {
        return ResponseEntity.ok(checkoutService.addProduct(sessionId, request));
    }

    @DeleteMapping("/{sessionId}/products")
    public ResponseEntity<CheckoutSessionResponse> removeProduct(
            @PathVariable UUID sessionId,
            @RequestBody ProductRemoveRequest request
    ) {
        return ResponseEntity.ok(checkoutService.removeProduct(sessionId, request));
    }

    @PostMapping("/{sessionId}/payment")
    public ResponseEntity<CheckoutReceiptResponse> processPayment(
            @PathVariable UUID sessionId,
            @RequestBody CheckoutPaymentRequest request
    ) {
        return ResponseEntity.ok(checkoutService.processPayment(sessionId, request));
    }
}
