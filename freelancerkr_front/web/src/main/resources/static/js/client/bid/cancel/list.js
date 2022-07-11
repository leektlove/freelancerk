$(document).ready(function() {
    $('button.amount').click(function() {
        if($(this).hasClass('disable')) {
            $(this).removeClass('disable');
            $(this).removeClass('up');
            $(this).addClass('down');
        } else if ($(this).hasClass('down')) {
            $(this).removeClass('down');
            $(this).addClass('up');
        } else if ($(this).hasClass('up')) {
            $(this).addClass('disable');
            $(this).removeClass('down');
            $(this).removeClass('up');
        }
    });

    $('button.deadline').click(function() {
        if($(this).hasClass('disable')) {
            $(this).removeClass('disable');
            $(this).removeClass('up');
            $(this).addClass('down');
        } else if ($(this).hasClass('down')) {
            $(this).removeClass('down');
            $(this).addClass('up');
        } else if ($(this).hasClass('up')) {
            $(this).addClass('disable');
            $(this).removeClass('down');
            $(this).removeClass('up');
        }
    });

    $('#refund_report').on('shown.bs.modal', function(event) {
        var totalPrize = 1*$(event.relatedTarget).siblings('input[name=totalPrize]').val();
        var totalChangedOptionMoney = 1*$(event.relatedTarget).siblings('input[name=totalChangedOptionMoney]').val();
        var totalSupplyAmount = 1*$(event.relatedTarget).siblings('input[name=totalSupplyAmount]').val();
        var totalVatAmount = 1*$(event.relatedTarget).siblings('input[name=totalVatAmount]').val();
        var totalDiscountAmount = 1*$(event.relatedTarget).siblings('input[name=totalDiscountAmount]').val();
        var totalPurchaseAmount = 1*$(event.relatedTarget).siblings('input[name=totalPurchaseAmount]').val();
        var numberOfApplyCount = 1*$(event.relatedTarget).siblings('input[name=numberOfApplyCount]').val();
        var refundablePrice = 1*$(event.relatedTarget).siblings('input[name=refundablePrice]').val();
        var firstPurchasedAt = $(event.relatedTarget).siblings('input[name=firstPurchasedAt]').val();
        var cancelAt = $(event.relatedTarget).siblings('input[name=cancelAt]').val();

        $('#refund_report .totalPrize').text(totalPrize.toLocaleString());
        $('#refund_report .totalChangedOptionMoney').text(totalChangedOptionMoney.toLocaleString());
        $('#refund_report .totalSupplyAmount').text(totalSupplyAmount.toLocaleString());
        $('#refund_report .totalVatAmount').text(totalVatAmount.toLocaleString());
        $('#refund_report .totalDiscountAmount').text(totalDiscountAmount.toLocaleString());
        $('#refund_report .totalPurchaseAmount').text(totalPurchaseAmount.toLocaleString());
        $('#refund_report .numberOfApplyCount').text(numberOfApplyCount.toLocaleString());
        $('#refund_report .refundablePrice').text(refundablePrice.toLocaleString());
        $('#refund_report .cancelAt').text(cancelAt);
        $('#refund_report .firstPurchasedAt').text(firstPurchasedAt);

    });
});

function projectDetail(projectId, sufix) {
    location.href = '/client/project/' + projectId + '/details' + (sufix?sufix:'');
}

function contestDetail(projectId, sufix) {
    location.href = '/client/contest/' + projectId + '/details' + (sufix?sufix:'');
}