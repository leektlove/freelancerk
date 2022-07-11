package com.freelancerk.service;

import com.freelancerk.domain.*;

import java.util.List;

public interface FreelancerPayService {

	void insertOptionTemp(PickMeUp pickMeUp, List<FreelancerPayProduct> payProductList);

	void purchasePickMeUp(Long userId, Long pickMeUpId, int usedPoints, User user, Purchase purchase);

	List<FreelancerProductItemType> getProductItemTypeList(String usageType);

	FreelancerProductItemType getProductItemById(Long itemId);

	void savePayProductList(List<FreelancerPayProduct> payProductList);

	List<FreelancerPayProduct> getUnPurchasedPayProductList(FreelancerPayProduct.Type type, Long pickMeUpId);

	void updatePurchase(Long pickMeUpId, List<FreelancerPayProduct> payProducts, Purchase purchase);

    void deleteUnPurchasedProductList(Long pickMeUpId);

	int countFreeProductCountOfPickMeUp(Long userId);
}
