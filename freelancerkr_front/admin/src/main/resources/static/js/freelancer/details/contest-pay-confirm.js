$(document).ready(function () {

    var userPoints = parseInt(removeComma($('#userPoints').val()));
    var totalMoney = parseInt(removeComma($('input[name=totalAmountOfMoney]').val()));
    var vatAmountOfMoney = parseInt(removeComma($('input[name=vatAmountOfMoney]').val()));
    var supplyAmountMoney = parseInt(removeComma($('input[name=supplyAmountOfMoney]').val()));

    var usedPoints = 0;

    // 사용 할 포인트 변화에 따라
    $('#btnPointAdaption').click(function (e) {
        var willUsePoints = parseInt(removeComma($('#inputWillUsePoints').val()));

        if (!willUsePoints) {
            willUsePoints = 0;
        }
        var diffPoints = userPoints - willUsePoints;
        if (diffPoints < 0) {
            alert("보유한 포인트보다 많은 포인트를 사용할 수 없습니다.");
            $('#inputWillUsePoints').val(0);
        } else if (willUsePoints > supplyAmountMoney) {
            $('#inputWillUsePoints').val(0);
            alert('공급가액 보다 많은 포인트를 사용할 수 없습니다.');
        } else {
            $('#usedPoints').val(willUsePoints);
            $('#currentPointsTxt').val(diffPoints.format());

            usedPoints = willUsePoints;
            var newSupplyAmountOfMoney = supplyAmountMoney - willUsePoints;
            var newVatMoney = Math.trunc(newSupplyAmountOfMoney * 0.1);
            var newTotalMoney = newSupplyAmountOfMoney + newVatMoney;
            $('input[name=totalAmountOfMoney]').val(newTotalMoney);
            $('input[name=supplyAmountOfMoney]').val(newSupplyAmountOfMoney);
            $('#supplyAmountOfMoneyTxt').text(newSupplyAmountOfMoney.format() + '원');
            $('input[name=vatAmountOfMoney]').val(newVatMoney);
            $('#vatAmountOfMoneyTxt').text(newVatMoney.format() + '원');
            $('#inputTotalAmountOfMoney').val(newTotalMoney.format());
        }
    });

    $('#btnPointCancel').click(function() {
        usedPoints = 0;
        $('#currentPointsTxt').text(userPoints.format());
        $('#inputWillUsePoints').val(0);
        $('input[name=totalAmountOfMoney]').val(totalMoney);
        $('input[name=supplyAmountOfMoney]').val(supplyAmountMoney);
        $('#supplyAmountOfMoneyTxt').text(supplyAmountMoney.format() + '원');
        $('input[name=vatAmountOfMoney]').val(vatAmountOfMoney);
        $('#vatAmountOfMoneyTxt').text(vatAmountOfMoney.format() + '원');
        $('#inputTotalAmountOfMoney').val(totalMoney.format());
    })
});

/**
 * Helper Functions
 */
// 총합 계산

// 숫자 타입에서 쓸 수 있도록 format() 함수 추가
Number.prototype.format = function () {
    if (this == 0) return 0;
    var reg = /(^[+-]?\d+)(\d{3})/;
    var n = (this + '');
    while (reg.test(n)) n = n.replace(reg, '$1' + ',' + '$2');
    return n;
};

// 문자열 타입에서 쓸 수 있도록 format() 함수 추가
String.prototype.format = function () {
    var num = parseFloat(this);
    if (isNaN(num)) return "0";
    return num.format();
};