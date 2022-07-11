package com.freelancerk.controller.io;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProjectProductOptionResponse {
    private int totalPrice;
    private int vat;
    private int totalChargedOptionPrice;
    private int totalDiscountOptionPrice;
    private List<OptionNamePrice> optionNamePriceList;
}
