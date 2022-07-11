$('#contestSpecModal').on('shown.bs.modal', function (event) {
    var projectId = $(event.relatedTarget).data('project-id');

    $.ajax({
        method: 'GET',
        url: '/purchases/projects/' + projectId,
        success: function (response) {
            var purchase = response.data;
            setContestPaymentDataInModal(purchase);
        },
        error: function (error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
            $('#contestSpecModal').modal('hide');
            console.error(error);
        }
    });
});

function setContestPaymentDataInModal(purchase) {
    var contest = purchase.project;
    var totalPrize = contest.prizeFor1st + contest.prizeFor2nd + contest.prizeFor3rd;
    var fee = 0;
    if (contest.payForFeeToFreelancer) {
        fee = Math.ceil(totalPrize * 0.1);
    }
    $('#contestSpecModal #confirmativePrice').text(totalPrize.toLocaleString() + '원');
    $('#contestSpecModal #depositMoney').text((100000).toLocaleString() + '원');
    $('#contestSpecModal #optionPrice').text(purchase.chargedOptionsAmountOfMoney.toLocaleString() + '원');
    $('#contestSpecModal #fee').text(fee.toLocaleString() + '원');
    $('#contestSpecModal #supplyAmount').text(purchase.supplyAmountOfMoney.toLocaleString() + '원');
    $('#contestSpecModal #vat').text(purchase.vatAmountOfMoney.toLocaleString() + '원');
    $('#contestSpecModal #totalPrice').text(purchase.totalAmountOfMoney.toLocaleString() + '원');
}