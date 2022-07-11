package com.freelancerk.controller;

import com.freelancerk.TimeUtil;
import com.freelancerk.domain.EscrowRefundRequest;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.EscrowRefundRequestRepository;
import com.freelancerk.io.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EscrowRefundRequestController {

    private EscrowRefundRequestRepository escrowRefundRequestRepository;

    @Autowired
    public EscrowRefundRequestController(EscrowRefundRequestRepository escrowRefundRequestRepository) {
        this.escrowRefundRequestRepository = escrowRefundRequestRepository;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/escrow-refund-requests/{id}")
    public CommonResponse<Void> updateEscrowLogStatus(@PathVariable("id") Long id,
                                                      @RequestParam(value = "refundedAt", required = false) String refundedAt,
                                                      @RequestParam(value = "type", required = false) EscrowRefundRequest.Type type,
                                                      @RequestParam(value = "businessType", required = false) User.BusinessType businessType,
                                                      @RequestParam(value = "cancelReceipt", required = false) Boolean cancelReceipt,
                                                      @RequestParam(value = "delayedTransfer", required = false) Boolean delayedTransfer) {
        EscrowRefundRequest refundRequest = escrowRefundRequestRepository.getOne(id);
        if (type != null) {
            refundRequest.setType(type);
        }

        if (businessType != null) {
            refundRequest.setBusinessType(businessType);
        }

        if (cancelReceipt != null) {
            refundRequest.setCancelReceipt(cancelReceipt);
        }

        if (delayedTransfer != null) {
            refundRequest.setDelayedTransfer(delayedTransfer);
        }

        if (StringUtils.isNotEmpty(refundedAt)) {
            refundRequest.setRefundedAt(TimeUtil.convertStrWithTimeToLocalDateTime(refundedAt));
        }
        escrowRefundRequestRepository.save(refundRequest);

        return CommonResponse.ok();
    }
}
