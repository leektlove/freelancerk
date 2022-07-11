$('#contestFreelancerSpecModal').on('shown.bs.modal', function (event) {
    var projectId = $(event.relatedTarget).data('project-id');

    $.ajax({
        method: 'GET',
        url: '/contests/' + projectId + '/payment-data/for-freelancer',
        success: function (response) {
            setContestFreelancerPaymentDataInModal(response.data);
        },
        error: function (error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
            $('#contestSpecModal').modal('hide');
            console.error(error);
        }
    });
});

function setContestFreelancerPaymentDataInModal(paymentData) {
    if (!paymentData) {
        alert('정보가 존재하지 않습니다.');
        $('#specModal').modal('hide');
        return;
    }
    $('#contestFreelancerSpecModal .tax-type').hide();
    if ('VAT' === paymentData.taxType) {
        $('#contestFreelancerSpecModal .tax-type.vat').show();
        $('#contestFreelancerSpecModal .tax-type.collection-in-advance').hide();

        $('#contestFreelancerSpecModal #vat').text(paymentData.vat.toLocaleString() + '원');
        $('#contestFreelancerSpecModal .colspanvar').attr('colspan', '2');
    } else if ('COLLECTION_IN_ADVANCE' === paymentData.taxType) {
        $('#contestFreelancerSpecModal .tax-type.vat').hide();
        $('#contestFreelancerSpecModal .tax-type.collection-in-advance').show();

        $('#contestFreelancerSpecModal #businessIncomeTax').text(paymentData.businessIncomeTax.toLocaleString() + '원');
        $('#contestFreelancerSpecModal #localIncomeTax').text(paymentData.localIncomeTax.toLocaleString() + '원');
        $('#contestFreelancerSpecModal .colspanvar').attr('colspan', '1');
    }
    $('#contestFreelancerSpecModal #totalDeductedAmount').text(paymentData.totalDeductedAmount.toLocaleString() + '원');
    $('#contestFreelancerSpecModal #freelancerName').text(paymentData.user.bankAccountName);
    $('#contestFreelancerSpecModal #freelancerBankAccountInfo').text(paymentData.user.paymentInfo);
    $('#contestFreelancerSpecModal #totalAmount').text(paymentData.amount.toLocaleString() + '원');
    $('#contestFreelancerSpecModal #fee').text(paymentData.fee.toLocaleString() + '원');
    $('#contestFreelancerSpecModal #realAmount').text(paymentData.realAmount.toLocaleString() + '원');
}