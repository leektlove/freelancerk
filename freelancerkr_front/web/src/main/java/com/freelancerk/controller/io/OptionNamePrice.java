package com.freelancerk.controller.io;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OptionNamePrice {
    private String optionName;
    private Long optionPrice;
    private String optionValidationSpan;
    private int optionCount;
    private boolean optionIncludedInPack;
}
