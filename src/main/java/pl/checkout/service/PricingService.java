package pl.checkout.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.checkout.model.CheckoutProduct;
import pl.checkout.model.CheckoutSession;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingService {

    public void applyPricing(CheckoutSession session) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CheckoutProduct checkoutProduct : session.getProducts()) {
            BigDecimal productPrice = checkoutProduct.getProduct().getPrice();

            checkoutProduct.setPrice(productPrice);
            // todo: remove this and implement real discount logic later on
            checkoutProduct.setDiscount(BigDecimal.ZERO);
            BigDecimal productFinalPrice = productPrice.subtract(checkoutProduct.getDiscount());
            checkoutProduct.setFinalPrice(productFinalPrice.max(BigDecimal.ZERO));

            BigDecimal productTotal = productPrice
                    .multiply(BigDecimal.valueOf(checkoutProduct.getQuantity()));
            totalAmount = totalAmount.add(productTotal);
        }

        BigDecimal finalAmount = totalAmount.subtract(totalDiscount);

        session.setTotalAmount(totalAmount);
        session.setTotalDiscount(totalDiscount);
        session.setFinalAmount(finalAmount.max(BigDecimal.ZERO));
    }
}
