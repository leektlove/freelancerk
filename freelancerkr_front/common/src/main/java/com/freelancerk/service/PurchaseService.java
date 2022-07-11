package com.freelancerk.service;

import com.freelancerk.domain.ProjectBid;
import com.freelancerk.domain.Purchase;

public interface PurchaseService {

    String makeAdministrationId(Long sessionUserId, Long projectId, Integer optionAmountOfMoney, Integer supplyAmountOfMoney, Integer totalDiscountOptionPrice, Integer vatAmountOfMoney, Integer totalAmountOfMoney, Purchase.Type type, String optionIdsCountMap, Integer usePoint, String mode);

    Purchase confirmPurchaseViaCallback(String administrationId, String impUid, boolean postingPeriodExtend, String postingEndAt);

    Purchase confirmPurchaseContestEntry(String administrationId, String impUid, String merchantUid, ProjectBid projectBid);

    Purchase confirmPurchasePickMeUp(Long sessionUserId, Purchase purchase);
}
