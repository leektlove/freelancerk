package com.freelancerk.controller.io;

import javax.persistence.Transient;

import com.freelancerk.domain.Project;
import com.freelancerk.domain.TaxType;
import com.freelancerk.domain.User;
import com.freelancerk.util.FeeCalculator;

import lombok.Data;

@Data
public class ContestPaymentData {

    private Project project;
    private int amount;
    private TaxType taxType;
    private User user;

    public int getFee() {
        if (project == null) return 0;
        if (Project.Type.CONTEST.equals(project.getProjectType())
                && project.getPayForFeeToFreelancer() != null && project.getPayForFeeToFreelancer()) {
            return 0;
        }
        return (int) (amount * 0.1);
    }

    public int getBusinessIncomeTax() {
        if (project == null) return 0;
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            int result = (int) ((amount - getFee()) * FeeCalculator.getFeeRate(project.getPostingStartAt()));
            return (result/10)*10;
        }
        return 0;
    }

    public int getLocalIncomeTax() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            int result = (int) (getBusinessIncomeTax()*0.1);
            return (result/10)*10;
        }
        return 0;
    }

    public int getVat() {
        if (TaxType.VAT.equals(taxType)) {
            return (int) ((amount - getFee())*0.1);
        }

        return 0;
    }

    @Transient
    public int getSupplyAmount() {
        return (int) (amount - getVat());
    }

    public int getTotalDeductedAmount() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            return getFee() + getBusinessIncomeTax() + getLocalIncomeTax();
        } else if (TaxType.VAT.equals(taxType)) {
            return getFee();
        }

        return 0;
    }

    public long getRealAmount() {
        if (TaxType.COLLECTION_IN_ADVANCE.equals(taxType)) {
            return amount - getTotalDeductedAmount();
        } else if (TaxType.VAT.equals(taxType)) {
            return amount - getTotalDeductedAmount() + getVat();
        }

        return 0;
    }
}
