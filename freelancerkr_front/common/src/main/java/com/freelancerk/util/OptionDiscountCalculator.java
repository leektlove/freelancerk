package com.freelancerk.util;

public class OptionDiscountCalculator {

    public static int getOptionDiscountAmountForClient(int chargedOptionCount, int totalOptionOriginalPrice) {
        int discountAmount = 0;

        if (chargedOptionCount == 2) {
            discountAmount = (int) (totalOptionOriginalPrice * 0.1);
        } else if (chargedOptionCount == 3) {
            discountAmount = (int) (totalOptionOriginalPrice * 0.15);
        } else if (chargedOptionCount >= 4) {
            discountAmount = (int) (totalOptionOriginalPrice * 0.20);
        }

        return discountAmount;
    }

    public static int getOptionDiscountAmountForFreelancer(int chargedOptionCount, int totalOptionOriginalPrice) {
        int discountAmount = 0;
        if (chargedOptionCount == 2) {
            discountAmount = (int) (totalOptionOriginalPrice * 0.1);
        } else if (chargedOptionCount == 3) {
            discountAmount = (int) (totalOptionOriginalPrice * 0.15);
        } else if (chargedOptionCount >= 4) {
            discountAmount = (int) (totalOptionOriginalPrice * 0.20);
        }

        return discountAmount;
    }
}
