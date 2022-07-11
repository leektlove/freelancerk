$(document).ready(function() {
    $('input[name=use-points]').keyup(function() {
        var points = removeComma($(this).val());
        var currentPoint = $(this).data('current-points');
        var maxPoints = (totalChargedOptionPrice > currentPoint)?1*currentPoint:1*totalChargedOptionPrice;
        if (points > maxPoints) {
            $('input[name=use-points]').val(maxPoints.toLocaleString());
        }
    });

    $('#btn-use-point').click(function() {
        applyPoint();
    });

    $('#btn-cancel-use-point').click(function() {
        if (!confirm('포인트 적용을 취소하시겠습니까?')) {
            return;
        }
        var userPoint = parseInt($('input[name=use-points]').data('current-points'));
        $('#userPointsTxt').text(userPoint.toLocaleString() + 'P');

        $('#use-points').html(0);
        $('#use-points').attr('data-use-points', 0);

        var supplyAmountOfMoney = $('#totalChargedOptionPrice').attr('data')*1;
        var vat = Math.trunc(supplyAmountOfMoney*0.1);
        var totalPrice = supplyAmountOfMoney + vat;

        $('.supplyAmountOfMoneyTxt').html(supplyAmountOfMoney.toLocaleString() + '원');
        $('.supplyAmountOfMoneyTxt').attr('data', supplyAmountOfMoney);

        $('.vatAmountOfMoney').html(vat.toLocaleString() + '원');
        $('.vatAmountOfMoney').attr('data', vat);

        $('.totalPrice').html(totalPrice.toLocaleString());
        $('.totalPrice').attr('data', totalPrice);
    });
});

function applyPoint() {
    $.ajax({
        type: 'GET',
        url: '/users/me/points',
        success: function (response) {
            var points = response.data;
            if (points === 0) {
                alert('누적된 포인트가 없습니다.');
                return;
            }
            var userPoint = parseInt($('input[name=use-points]').data('current-points'));
            var usePoint = 1*removeComma($('input[name=use-points]').val());
            $('#userPointsTxt').text((userPoint - usePoint).toLocaleString() + 'P');

            $('#use-points').html(usePoint.toLocaleString());
            $('#use-points').attr('data-use-points', usePoint);
            var supplyAmountOfMoney = $('#totalChargedOptionPrice').attr('data')*1 - usePoint;
            var vat = Math.trunc(supplyAmountOfMoney*0.1);
            var totalPrice = supplyAmountOfMoney + vat;

            $('.supplyAmountOfMoneyTxt').html(supplyAmountOfMoney.toLocaleString()+'원');
            $('.supplyAmountOfMoneyTxt').attr('data', supplyAmountOfMoney);

            $('.vatAmountOfMoney').html(vat.toLocaleString()+'원');
            $('.vatAmountOfMoney').attr('data', vat);

            $('.totalPrice').html(totalPrice.toLocaleString());
            $('.totalPrice').attr('data', totalPrice);

        },
        error: function() {
            alert('포인트 조회에 실패했습니다. 문제가 계속될 경우 고객센터로 문의주세요.');
        }
    });
}