package pl.checkout.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.checkout.model.*;
import pl.checkout.repository.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PromotionRepository promotionRepository;

    public void applyPricing(CheckoutSession session) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        Map<UUID, BigDecimal> discountPerItem = findPromotions(session);

        for (CheckoutProduct checkoutProduct : session.getProducts()) {
            final Product product = checkoutProduct.getProduct();
            BigDecimal productPrice = product.getPrice();

            // Set original price
            checkoutProduct.setPrice(productPrice);

            // Set discount amount for 1 product
            BigDecimal discount = discountPerItem.getOrDefault(product.getId(), BigDecimal.ZERO);
            checkoutProduct.setDiscount(discount);

            // Set final price for 1 product after discount
            BigDecimal productFinalPrice = productPrice.subtract(discount);
            checkoutProduct.setFinalPrice(productFinalPrice.max(BigDecimal.ZERO));

            // Add to total checkout price
            BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(checkoutProduct.getQuantity()));

            totalAmount = totalAmount.add(productTotal);
        }

        BigDecimal totalDiscount = calculateDiscounts(session, discountPerItem);

        BigDecimal finalAmount = totalAmount.subtract(totalDiscount);

        session.setTotalAmount(totalAmount);
        session.setTotalDiscount(totalDiscount);
        session.setFinalAmount(finalAmount.max(BigDecimal.ZERO));
    }

    private BigDecimal calculateDiscounts(CheckoutSession session, Map<UUID, BigDecimal> discountPerItem) {
        Map<UUID, Integer> checkoutQuantities = session.getProducts().stream().collect(Collectors.toMap(cp -> cp.getProduct().getId(), CheckoutProduct::getQuantity, Integer::sum));

        Set<UUID> productIds = checkoutQuantities.keySet();
        List<Promotion> candidatePromotions = promotionRepository.findByProductIds(productIds);

        List<Promotion> promotions = candidatePromotions.stream().filter(promotion -> canApplyPromotion(promotion, checkoutQuantities, 1)).toList();

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Promotion promotion : promotions) {
            if (promotion.isBundlePromotion()) {
                int bundleCount = promotion.getProducts().stream().mapToInt(pp -> checkoutQuantities.getOrDefault(pp.getProduct().getId(), 0) / pp.getRequiredQuantity()).min().orElse(0);

                BigDecimal bundleDiscount = promotion.getProducts().stream().map(PromotionProduct::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(bundleCount));

                totalDiscount = totalDiscount.add(bundleDiscount);
            } else {
                int multiplier = calculateMultiplier(promotion, checkoutQuantities);
                for (PromotionProduct promotionProduct : promotion.getProducts()) {
                    UUID productId = promotionProduct.getProduct().getId();
                    int productQuantity = checkoutQuantities.get(productId);

                    BigDecimal quantityDiscount = promotionProduct.getDiscountAmount().multiply(BigDecimal.valueOf(productQuantity)).multiply(BigDecimal.valueOf(multiplier));

                    totalDiscount = totalDiscount.add(quantityDiscount);
                }
            }
        }

        return totalDiscount;
    }

    private Map<UUID, BigDecimal> findPromotions(CheckoutSession session) {
        Map<UUID, Integer> checkoutQuantities = session.getProducts().stream().collect(Collectors.toMap(cp -> cp.getProduct().getId(), CheckoutProduct::getQuantity, Integer::sum));

        Set<UUID> productIds = checkoutQuantities.keySet();
        List<Promotion> candidatePromotions = promotionRepository.findByProductIds(productIds);

        List<Promotion> promotions = candidatePromotions.stream().filter(promotion -> canApplyPromotion(promotion, checkoutQuantities, 1)).toList();

        Map<UUID, BigDecimal> promotionsData = new HashMap<>();

        for (Promotion promotion : promotions) {
            if (promotion.isBundlePromotion()) {
                applyBundlePromotion(promotion, checkoutQuantities, promotionsData);
            } else {
                applyQuantityPromotion(promotion, checkoutQuantities, promotionsData);
            }
        }

        return promotionsData;
    }

    private void applyBundlePromotion(Promotion promotion, Map<UUID, Integer> checkoutQuantities, Map<UUID, BigDecimal> promotionsData) {
        int bundleCount = promotion.getProducts().stream().mapToInt(pp -> checkoutQuantities.getOrDefault(pp.getProduct().getId(), 0) / pp.getRequiredQuantity()).min().orElse(0);

        if (bundleCount > 0) {
            for (PromotionProduct promotionProduct : promotion.getProducts()) {
                UUID productId = promotionProduct.getProduct().getId();
                BigDecimal totalDiscountForProduct = promotionProduct.getDiscountAmount().multiply(BigDecimal.valueOf(bundleCount));

                int productQuantityInCart = checkoutQuantities.get(productId);
                BigDecimal discountPerItem = totalDiscountForProduct.divide(BigDecimal.valueOf(productQuantityInCart), 2, RoundingMode.HALF_UP);

                promotionsData.merge(productId, discountPerItem, BigDecimal::add);
            }
        }
    }

    private void applyQuantityPromotion(Promotion promotion, Map<UUID, Integer> checkoutQuantities, Map<UUID, BigDecimal> promotionsData) {
        int multiplier = calculateMultiplier(promotion, checkoutQuantities);

        for (PromotionProduct promotionProduct : promotion.getProducts()) {
            UUID productId = promotionProduct.getProduct().getId();

            if (multiplier >= 1) {
                promotionsData.merge(productId, promotionProduct.getDiscountAmount(), BigDecimal::add);
            }
        }
    }

    private int calculateMultiplier(Promotion promotion, Map<UUID, Integer> checkoutQuantities) {
        int multiplier = 1;
        while (canApplyPromotion(promotion, checkoutQuantities, multiplier + 1)) {
            multiplier++;
        }
        return multiplier;
    }

    private boolean canApplyPromotion(Promotion promotion, Map<UUID, Integer> checkoutQuantities, int multiplier) {
        return promotion.getProducts().stream().allMatch(promotionProduct -> {
            UUID productId = promotionProduct.getProduct().getId();
            int requiredQuantity = promotionProduct.getRequiredQuantity() * multiplier;
            int availableQuantity = checkoutQuantities.getOrDefault(productId, 0);
            return availableQuantity >= requiredQuantity;
        });
    }
}