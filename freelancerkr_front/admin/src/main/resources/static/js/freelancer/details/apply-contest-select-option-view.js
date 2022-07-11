$(document).ready(function () {

    $('#submitWithoutPay').click(function() {
        var isChecked = $('#chkPolicy').is(":checked");
        if (!isChecked) {
            alert("이용약관에 동의해주십시오.");
        } else {
            $('input[name=payment]').val(false);
            $("#submit").trigger('click');
        }
    });
    $("input[name=freelancerProductItemTypeId\\[\\]]").change(function (e) {
        // 상품 옵션에 체크가 되거나 풀릴 때 마다 총 결제액을 계산한다.
        var checked = $(this).is(':checked');
        var usedInPickMeUp = $(this).data('used-in-pickmeup');
        var existsTicket = $(this).data('exists-ticket');
        if (!checked && existsTicket) {
            $(this).prop('checked', true);
            return;
        }
        if (!checked && usedInPickMeUp) {
            if(!confirm('무료로 이용하실 수 있습니다. 이용을 취소하시겠습니까?')) {
                $(this).prop('checked', true);
                return;
            } else {
                return;
            }
        }
        calculateSumOfProductMoney();
    });

    $('#submit').click(function () {
        var selectedProductList = [];

        $("input[name=freelancerProductItemTypeId\\[\\]]").each(function (index, element) {
            var isDisabled = $(element).attr("disabled");
            var isChecked = $(element).is(":checked");
            var usedInPickMeUp = JSON.parse($(element).data('used-in-pickmeup'));
            var existsTicket = $(this).data('exists-ticket');

            if (!existsTicket && isDisabled === undefined && isChecked === true) {
                var productId = $(element).data('product-id');
                var count = 1;
                var price = $(element).data('unit-price');
               
                selectedProductList.push({
                    "freelancerProductItemTypeId": productId,
                    "count": count,
                    "amount": price,
                    "usedInPickMeUp": usedInPickMeUp
                });
            }
        });

        $('input[name=payment]').val(true);

        var jsonSelectedProductList = JSON.stringify(selectedProductList);
        $('#inputSelectedProductList').val(jsonSelectedProductList);
    });
});

/**
 * Helper Functions
 */
// 총합 계산
function calculateSumOfProductMoney() {
    var sum = 0;
    var numberOfOptionCount = 0;
    $("input[name=freelancerProductItemTypeId\\[\\]]").each(function (index, element) {

        var isDisabled = $(element).attr("disabled");
        var isChecked = $(element).is(":checked");
        var usedInPickMeUp = $(this).data('used-in-pickmeup');
        var existsTicket = $(this).data('exists-ticket');

        if (!existsTicket && !usedInPickMeUp && isDisabled === undefined && isChecked === true) {
            var price = $(element).data('unit-price');
            numberOfOptionCount++;
            sum = sum + parseInt(price);
        }
    });
    if (numberOfOptionCount >= 4) {
        sum -= Math.trunc(sum*0.2);
    } else if (numberOfOptionCount === 3) {
        sum -= Math.trunc(sum*0.15);
    } else if (numberOfOptionCount === 2) {
        sum -= Math.trunc(sum*0.1);
    }
    $("#sumOfProductMoney").val(sum.format());
}



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
