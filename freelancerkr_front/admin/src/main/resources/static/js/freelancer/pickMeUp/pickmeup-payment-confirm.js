$(document).ready(function () {
    IMP.init(iamportMerchantId);

    /**
     * 처음 시작
     */
    // 대분류 목록 가져오기

    /**
     * 동적 변경
     */

    var userPoints = parseInt(removeComma($('#userPoints').val()));
    var totalMoney = parseInt(removeComma($('#totalAmountOfMoney').val()));
    var supplyAmountOfMoney = parseInt(removeComma($('#supplyAmountOfMoney').val()));
    var newPoint = parseInt(removeComma($('input[name=newPoint]').val()));

    var vat = Math.trunc(1.1*supplyAmountOfMoney);

    var usedPoints = 0;

    // 사용 할 포인트 변화에 따라
    $('#btnPointAdaption').click(function (e) {
        var willUsePoints = parseInt(removeComma($('#inputWillUsePoints').val()));
        if (!willUsePoints) {
            willUsePoints = 0;
        }
        var diffPoints = userPoints - willUsePoints;
        if (diffPoints < 0) {
            $('#inputWillUsePoints').val(userPoints);
            alert("보유한 포인트보다 많은 포인트를 사용할 수 없습니다.")
            return;
            // console.log("보유한 포인트보다 많은 포인트를 사용할 수 없습니다.");
        }
        if (supplyAmountOfMoney < willUsePoints) {
            alert("옵션 금액 보다 더 많은 포인트를 사용할 수 없습니다.");
            $('#inputWillUsePoints').val('');
            return;
        }
        var newSupplyAmountOfMoney = supplyAmountOfMoney - willUsePoints;
        $('#userPointsTxt').text(($('#userPoints').val() - willUsePoints).toLocaleString());
        $('#usedPoints').val(willUsePoints);
        $('#usedPoints').data('point', willUsePoints);

        var newVat = Math.trunc(newSupplyAmountOfMoney * 0.1);
        var newTotalMoney = newSupplyAmountOfMoney;
        // var newTotalMoney = newSupplyAmountOfMoney + newVat;
        $('#totalAmountOfMoney').val(newTotalMoney);
        $('#totalAmountOfMoney').data('price', newTotalMoney);
        $('#supplyAmountOfMoneyTxt').text(newSupplyAmountOfMoney.format() + '원');
        $('#supplyAmountOfMoney').val(newSupplyAmountOfMoney);
        $('#vatAmountOfMoneyTxt').text(newVat.format() + '원');
        $('#vatAmountOfMoney').val(newVat);
        $('#inputTotalAmountOfMoney').data('price', newTotalMoney);
        $('#inputTotalAmountOfMoney').text(newTotalMoney.format() + '원');
        $('#newPointTxt').text(Math.trunc(0.1*newSupplyAmountOfMoney).toLocaleString() + ' P');
    });

    $('#btnPointCancel').click(function() {
        $('#inputWillUsePoints').val(0);
        $('#totalAmountOfMoney').val(totalMoney);
        $('#totalAmountOfMoney').data('price', totalMoney);
        $('#supplyAmountOfMoneyTxt').text(supplyAmountOfMoney.format()+'원');
        $('#supplyAmountOfMoney').val(supplyAmountOfMoney);
        $('#vatAmountOfMoneyTxt').text(vat.format()+'원');
        $('#vatAmountOfMoney').val(vat);
        $('#inputTotalAmountOfMoney').data('price', totalMoney);
        $('#inputTotalAmountOfMoney').text(totalMoney.format() +'원');

        $('#newPointTxt').text(newPoint.toLocaleString() + ' P');
        $('#userPointsTxt').text(parseInt($('input#userPoints').val()).toLocaleString());
    });

    $('#btnPayConfirm').click(function (e) {
        var isChecked = $('#chkPolicy').is(":checked");
        if (!isChecked) {
            alert("이용약관에 동의해주십시오.");
            return;
        }

        var merchantUid = 'merchant_' + new Date().getTime(); // todo

        var totalAmountOfMoney = $('#totalAmountOfMoney').data('price')*1;
        var usePoint = $('#usedPoints').data('point');

        if (totalAmountOfMoney === 0) {
            alert('결제하실 금액이 없으므로 결제 없이 바로 등록 완료되었습니다. 수정 및 옵션추가 등은 [포트폴리오] 관리에서 가능합니다.');
            $('input[name=merchantUid]').val(merchantUid);

            $("#submit").trigger('click');

            return;
        }

        if (totalAmountOfMoney !== 0 && totalAmountOfMoney <= 100) {
            alert('100원 이하로는 결제가 불가능합니다.');
            return;
        }

        IMP.request_pay({
            pg : 'danal',
            pay_method : 'card',
            merchant_uid : merchantUid,
            name : '[프리랜서코리아] 포트폴리오(' + pickMeUpTitle + ') 유료 옵션 결제',
            amount :  totalAmountOfMoney,
            buyer_email : userEmail,
            buyer_name : userName,
            buyer_tel : userCellphone
        }, function(rsp) {
            if ( rsp.success ) {
                var msg = '결제가 완료되었습니다.';
                $('input[name=impUid]').val(rsp.imp_uid);
                $('input[name=merchantUid]').val(merchantUid);

                $("#submit").trigger('click');

            } else {
                var msg = '결제에 실패하였습니다.';
                msg += '에러내용 : ' + rsp.error_msg;
            }
            alert(msg);
        });
    });
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