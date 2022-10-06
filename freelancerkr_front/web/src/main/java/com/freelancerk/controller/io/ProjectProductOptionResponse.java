package com.freelancerk.controller.io;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProjectProductOptionResponse {
    private int totalPrice;
    private int vat;
    private int totalChargedOptionPrice;
    private int totalDiscountOptionPrice;
    private List<OptionNamePrice> optionNamePriceList;
}
