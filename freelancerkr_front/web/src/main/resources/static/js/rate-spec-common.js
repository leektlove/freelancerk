$(document).ready(function () {

    $('#ratedModal').on('shown.bs.modal', function (event) {
        var projectId = $(event.relatedTarget).data('project-id');
        var raterType = $(event.relatedTarget).data('rater-type');

        $.ajax({
            method: 'GET',
            url: '/projects/' + projectId + '/rates?raterType=' + raterType,
            success: function (data) {
                var projectRateData = data.data;
                if (!projectRateData) {
                    $('#ratedModal').modal('hide');
                    alert('평가가 없습니다.');
                    return;
                }
                $('#ratedModal #type1RateGraph').css('width', 100 * projectRateData.type1Rate / 5 + '%');
                $('#ratedModal #type2RateGraph').css('width', 100 * projectRateData.type2Rate / 5 + '%');
                $('#ratedModal #type3RateGraph').css('width', 100 * projectRateData.type3Rate / 5 + '%');
                $('#ratedModal #type4RateGraph').css('width', 100 * projectRateData.type4Rate / 5 + '%');
                $('#ratedModal #type5RateGraph').css('width', 100 * projectRateData.type5Rate / 5 + '%');

                $('#ratedModal #type1RateNumber').text(projectRateData.type1Rate);
                $('#ratedModal #type2RateNumber').text(projectRateData.type2Rate);
                $('#ratedModal #type3RateNumber').text(projectRateData.type3Rate);
                $('#ratedModal #type4RateNumber').text(projectRateData.type4Rate);
                $('#ratedModal #type5RateNumber').text(projectRateData.type5Rate);

                $('#ratedModal .rateContent').text(projectRateData.content);
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
                $('#ratedModal').modal('hide');
                console.error(error);
            }
        })
    });

    $('#ratingModal').on('shown.bs.modal', function (event) {
        var projectId = $(event.relatedTarget).data('project-id');
        var raterType = $(event.relatedTarget).data('rater-type');

        $.ajax({
            method: 'GET',
            url: '/projects/' + projectId + '/rates?raterType=' + raterType,
            success: function (data) {
                var projectRateData = data.data;
                if (!projectRateData) {
                    $('#ratingModal').modal('hide');
                    alert('평가가 없습니다.');
                    return;
                }
                $('#ratingModal .type1RateGraph').css('width', 100 * projectRateData.type1Rate / 5 + '%');
                $('#ratingModal .type2RateGraph').css('width', 100 * projectRateData.type2Rate / 5 + '%');
                $('#ratingModal .type3RateGraph').css('width', 100 * projectRateData.type3Rate / 5 + '%');
                $('#ratingModal .type4RateGraph').css('width', 100 * projectRateData.type4Rate / 5 + '%');
                $('#ratingModal .type5RateGraph').css('width', 100 * projectRateData.type5Rate / 5 + '%');

                $('#ratingModal .type1RateNumber').text(projectRateData.type1Rate);
                $('#ratingModal .type2RateNumber').text(projectRateData.type2Rate);
                $('#ratingModal .type3RateNumber').text(projectRateData.type3Rate);
                $('#ratingModal .type4RateNumber').text(projectRateData.type4Rate);
                $('#ratingModal .type5RateNumber').text(projectRateData.type5Rate);

                $('#ratingModal .rateContent').text(projectRateData.content);
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
                $('#ratingModal').modal('hide');
                console.error(error);
            }
        })
    });

    $('#specModal').on('shown.bs.modal', function (event) {
        var projectId = $(event.relatedTarget).data('project-id');
        var paymentToUserId = $(event.relatedTarget).data('id');
        var amount = $(event.relatedTarget).data('amount');

        if (!amount || amount === 0) {
            if (!loginAsClient) {
                alert('프로젝트 수입금액이 없습니다.');
                $('#specModal').modal('hide');
                return;
            } else {
                alert('프로젝트 지출금액이 없습니다.');
                $('#specModal').modal('hide');
                return;
            }
        }

        var url = paymentToUserId?('/payment-to-users/' + paymentToUserId):('/payment-to-users?type=PROJECT&projectId=' + projectId);
        $.ajax({
            method: 'GET',
            url: url,
            success: function (data) {
                if ('SUCCESS' === data.responseCode) {
                    var paymentData = data.data;
                    setPaymentDataInModal(paymentData);
                } else {
                    alert(data.message);
                    $('#specModal').modal('hide');
                }
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
                $('#specModal').modal('hide');
                console.error(error);
            }
        });
    });
});