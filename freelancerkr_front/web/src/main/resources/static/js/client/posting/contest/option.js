var totalPrize = minPrize;
var fee = 0;
var prizeFor1stElem;
var prizeFor2ndElem;
var prizeFor3rdElem;
var payForFeeToFreelancerYnElem;
var feeElem;
var incentiveYnElem;
var incentivePrizeElem;
var totalPrizeElem;

var finalTotalPrizeElem;
var finalPrizeFor1stElem;
var finalPrizeFor2ndElem;
var finalPrizeFor3rdElem;
var finalIncentivePriceElem;
var finalTotalPostingOptionElem;
var finalSelectedOptionsContainer;
var finalUsePointsElem;
var finalSupplyPriceElem;
var finalVatElem;
var totalChargedPriceElem;

var totalOptionPrice = 0;
var depositMoeny = 100000;
var usedPoints = 0;

var postingEndMoment;
var optionMaxDate;
var previousPostingEndAt;

var optionMaxDate;
var optionIdAddAgreeDic = {};
var postingEndAtChangeByCode = false;

$(document).ready(function() {

    if ('EXTEND' !== mode) {
        alert(' [주의] 컨테스트 확정상금(기본상금+인센티브금액)은 포스팅이 완료된 후에는 수정이 불가능합니다. 따라서 이용자분께서는 본 단계부터 더욱 신중히 입력해주십시오. ');
    }

    $('#postingStartAt').on('changeDate', function(ev){
        $(this).datepicker('hide');
    });

    $('#postingEndAt').on('show', function(ev){
        var changedDate = $(ev.currentTarget).val();
        previousPostingEndAt = changedDate;
    });

    $('#postingEndAt').on('changeDate', function(ev){
        if (postingEndAtChangeByCode) {
            postingEndAtChangeByCode = false;
            return;
        }
        var changedDate = $(ev.currentTarget).val();
        if (getCheckedChargedOptionCount() > 0 && optionMaxDate && moment(changedDate).startOf('day').diff(optionMaxDate) < 0 ) {
            alert('포스팅 기간이 선택하신 옵션상품의 만기일보다 짧습니다. 변경을 원하시면 옵션의 기한을 조정해주세요.');
            ev.preventDefault();

            postingEndAtChangeByCode = true;
            $('#postingEndAt').datepicker('setDate', moment(previousPostingEndAt).format('YYYY-MM-DD'));
            return;
        }
        postingEndMoment = moment(changedDate);
        syncPostingEndAt(moment(changedDate).format('YYYY.MM.DD'));
        $(this).datepicker('hide');
    });



    $('#postingStartAt').datepicker({defaultDate: moment().zone('+0900')}).datepicker('setDate', moment().startOf('day').format('YYYY-MM-DD'));
    //
    $('#postingEndAt').datepicker({
        defaultDate: moment().zone('+0900'),
        changeYear: true,
        startDate: '+1D',
        endDate: moment().add(2, 'weeks').format('YYYY-MM-DD'),
        language: "ko",
        autoclose:true}).datepicker('setDate', moment().add(2, 'weeks').format('YYYY-MM-DD'));

    incentiveYnElem = $('input:radio[name=incentive]');
    incentivePrizeElem = $('input[name=incentivePrize]');
    totalPrizeElem = $('.totalPrize');
    prizeFor1stElem = $('input[name=prizeFor1st]');
    prizeFor2ndElem = $('input[name=prizeFor2nd]');
    prizeFor3rdElem = $('input[name=prizeFor3rd]');

    payForFeeToFreelancerYnElem = $('input[name=payForFeeToFreelancer]');
    feeElem = $('#fee');

    finalTotalPrizeElem = $('.bill.totalPrize');
    finalPrizeFor1stElem = $('.bill.prizeFor1st');
    finalPrizeFor2ndElem = $('.bill.prizeFor2nd');
    finalPrizeFor3rdElem = $('.bill.prizeFor3rd');
    finalIncentivePriceElem = $('.bill.incentivePrice');
    finalTotalPostingOptionElem = $('.bill.totalPostingOption');
    finalSelectedOptionsContainer = $('#selected-option-container');
    finalUsePointsElem = $('input[name=use-points]');
    finalSupplyPriceElem = $('.bill.supplyPrice');
    finalVatElem = $('.bill.vat');
    totalChargedPriceElem = $('.totalChargedPrice');

    $('input[name=contestProductItemTypeId\\[\\]][value=2]').prop('checked', true);


    incentiveYnElem.change(function() {
        if (this.value === 'true') {
            incentivePrizeElem.prop('disabled', false);
            totalPrize = removeComma(minPrize)*1 + removeComma(incentivePrizeElem.val())*1;
        }
        else if (this.value === 'false') {
            incentivePrizeElem.val(0);
            incentivePrizeElem.prop('disabled', true);
            totalPrize = removeComma(minPrize)*1;
        }
        setPrize();
        calculatePrice();
    });

    incentivePrizeElem.change(function() {
        if (incentiveYnElem.is(':checked')) {
            $('.prize-input-container input').val('');
            totalPrize = removeComma(minPrize)*1 + removeComma($(this).val())*1;
        } else {
            totalPrize = removeComma(minPrize)*1;
        }
        setPrize();
        calculatePrice();
    });

    $('input[name=contestProductItemTypeId\\[\\]]').change(function() {

    });

    prizeFor1stElem.change(function() {
        checkPrize();
    });

    prizeFor2ndElem.change(function() {
        checkPrize();
    });

    prizeFor3rdElem.change(function() {
        checkPrize();
    });

    payForFeeToFreelancerYnElem.change(function() {
        setFee();
        calculatePrice();
    });

    $('input[type=radio][name=pickType]').change(function() {
        var pickedValue = $(this).val();

        if (pickedValue === '1') {
            $('.container-1st').prop('disabled', true);
            $('.container-2nd').prop('disabled', true);
            $('.container-3rd').prop('disabled', true);
        } else if (pickedValue === '2') {
            $('.container-1st').prop('disabled', false);
            $('.container-2nd').prop('disabled', false);
            $('.container-3rd').prop('disabled', true);
        } else if (pickedValue === '3') {
            $('.container-1st').prop('disabled', false);
            $('.container-2nd').prop('disabled', false);
            $('.container-3rd').prop('disabled', false);
        }
        $('.prize-input-container input').val('');
    });


    $( ".contest-container .plus" ).click(function() {
        var checked = $(this).closest('tr').find('input[type=checkbox]').is(':checked');
        if (!checked) {
            alert('옵션을 먼저 선택해주세요.');
            return;
        }

        var previousPurchased = $(this).closest('tr').find('input[type=checkbox]').data('previous-checked');
        var productId = $(this).closest('tr').find('input[type=checkbox]').val();

        if (!optionIdAddAgreeDic[productId] && previousPurchased) {
            if(!confirm('기존에 구매하신 옵션입니다. 연장을 원하시면 확인을 클릭해주세요')) {
                return;
            }
            optionIdAddAgreeDic[productId] = true;
        }

        var optionAddTriggered = $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered');

        var week = +$(this).siblings(".data-count").data('count');
        var bill = $(this).closest('tr').find('.bill');
        var billVal = +removeComma(bill.text());

        if (week >= 2) {
            alert("기간설정은 2주 까지 가능합니다.");
            return;
        }

        var spanWeek=$(this).siblings("i");

        var defaultVal = (billVal/week);
        var weekVal=week+1;

        if (previousPurchased && !optionAddTriggered) {
            $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered', true);
            weekVal -= 1;
        }

        spanWeek.text(weekVal);

        bill.text(comma(defaultVal*weekVal));

        var $countInput = $(this).closest('tr').find('input[name=projectPaymentOptionCount\\[\\]]');
        $countInput.val(weekVal);
        $(this).siblings(".data-count").data('count', weekVal);
        $(this).siblings(".data-count").val(weekVal);

        $($(this).closest('tr').find('input[name=projectPaymentOptionId\\[\\]]')[0]).data('price', defaultVal*weekVal);

        if (previousPurchased) {
            var previousEndAt = moment($(this).closest('tr').find('input[type=checkbox]').data('previous-end-at'));
            $(this).closest('.product-quantity').siblings('.product-limit ').find('.option-limit').text(previousEndAt.add(weekVal, 'weeks').format('YYYY.MM.DD'));
        } else {
            $(this).closest('.product-quantity').siblings('.product-limit ').find('.option-limit').text(moment().add(weekVal, 'weeks').format('YYYY.MM.DD'));
        }
        setMaxOptionCategoryDateAndMaxDate(false);
        calculatePrice();

        insertOptionNames();
    });

    $( ".contest-container .minus" ).click(function() {
        var checked = $(this).closest('tr').find('input[type=checkbox]').is(':checked');
        if (!checked) {
            alert('옵션을 먼저 선택해주세요.');
            return;
        }

        var week = +$(this).siblings(".data-count").data('count');
        var bill = $(this).closest('tr').find('.bill');
        var billVal = +removeComma(bill.text());
        var weekVal;

        var productId = $(this).closest('tr').find('input[type=checkbox]').val();
        var previousPurchased = $(this).closest('tr').find('input[type=checkbox]').data('previous-checked');
        var optionAddTriggered = $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered');

        if (!previousPurchased) {
            if (week <= 1) {
                alert("기간설정은 1주 부터 52주까지 가능합니다.");
                return;
            }
        } else {
            if (optionAddTriggered && previousPurchased && week === 1) {
                $(this).closest('tr').find('input[name=projectPaymentOptionCount\\[\\]]').val(0);
                $(this).siblings(".data-count").data('count', 1);
                $(this).siblings(".data-count").val(1);
                $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered', false);
                optionIdAddAgreeDic[productId] = false;
                var previousEndAt = $(this).closest('tr').find('input[type=checkbox]').data('previous-end-at');
                $(this).closest('.product-quantity').siblings('.product-limit ').find('.option-limit').text(moment(previousEndAt).format('YYYY.MM.DD'));
                setMaxOptionCategoryDateAndMaxDate(true);
                calculatePrice();
                return;
            } else if (!optionAddTriggered && week <= 1) {
                alert("기간설정은 1주 부터 52주까지 가능합니다.");
                return;
            }
        }
        if((!previousPurchased && week <= 1) || (optionAddTriggered && previousPurchased && week <= 0)){
            alert("기간설정은 1주 부터 52주까지 가능합니다.");
            return;

        } else{
            var spanWeek=$(this).siblings("i");

            var defaultVal = (billVal/week);
            weekVal=week-1;
            spanWeek.text(weekVal);

            bill.text(comma(defaultVal*weekVal));
            $($(this).closest('tr').find('input[name=projectPaymentOptionId\\[\\]]')[0]).data('price', defaultVal*weekVal);

            var $countInput = $(this).closest('tr').find('input[name=projectPaymentOptionCount\\[\\]]');
            $countInput.val(weekVal);
            $(this).siblings(".data-count").data('count', weekVal);
            $(this).siblings(".data-count").val(weekVal);
            $(this).closest('.product-quantity').siblings('.product-limit ').find('.option-limit').text(moment().add(weekVal, 'weeks').format('YYYY.MM.DD'));
            setMaxOptionCategoryDateAndMaxDate(true);
            calculatePrice();

            insertOptionNames();
        }
    });

    $('input[name=projectPaymentOptionId\\[\\]]').change(function() {
        var checked = $(this).is(':checked');
        var previousChecked = $(this).data('previous-checked');
        if (previousChecked) {
            $(this).prop('checked', true);
            return;
        }
        if (checked) {
            var $countInput = $(this).siblings('input[name=projectPaymentOptionCount\\[\\]]');
            $countInput.val(1);
            $(this).closest('tr').find('.product-limit ').find('.option-limit').show();
            var price = 1*$(this).data('price');
            if (price > 0) {
                var count = $(this).closest('tr').find('.product-quantity').find('.data-count').data('count');
                $(this).closest('tr').find('.product-limit ').find('.option-limit').text(moment().add(count, 'weeks').format('YYYY.MM.DD'));

                $($(this).closest('tr').find('input[name=projectPaymentOptionId\\[\\]]')[0]).data('price', price*1);
                $(this).closest('.product-quantity').siblings('.product-limit ').find('.option-limit').text(moment().add(1, 'weeks').format('YYYY.MM.DD'));

            } else {
                var internalOptionLimit = $('.option-limit[data-option-code=INTERNAL]').text();
                $(this).closest('tr').find('.product-limit ').find('.option-limit').text(internalOptionLimit);
            }
        } else {
            var price = $(this).data('price');
            var unitPrice = $(this).data('unit-price');
            var optionCode = $(this).data('option-code');
            var previousChecked = $(this).data('previous-checked');
            if (price && 1*price > 0 && previousChecked) {
                if(!confirm('아직 기한이 남아있는 옵션입니다. 선택박스를 해제하시면 해당 옵션의 기능은 적용되지 않으며, 잔여 기간에 관계없이 재이용 또는 환불이 불가능합니다. 계속 진행하시겠습니까? ')) {
                    $(this).prop('checked', true);
                    return false;
                }
            }

            if (previousChecked && optionCode === 'EXTERNAL') {
                if(!confirm('선택박스를 해제하시면 더 이상 메인페이지에 노출되지 않습니다. 계속 진행하시겠습니까?')) {
                    $(this).prop('checked', true);
                    return false;
                }
            }


            var bill = $(this).closest('tr').find('.bill');
            var spanWeek=$(this).closest('tr').find('.product-quantity').find("i");
            spanWeek.text(1);

            bill.text(unitPrice.toLocaleString());
            $(this).data('price', unitPrice);

            var $countInput = $(this).siblings('input[name=projectPaymentOptionCount\\[\\]]');
            $countInput.val(1);
            $(this).closest('tr').find('.product-quantity').find(".data-count").data('count', 1);
            $(this).closest('tr').find('.product-quantity').find(".data-count").val(1);
            $(this).closest('tr').find('.product-limit').find('.option-limit').hide();

            if (optionCode === 'EXTERNAL' || optionCode === 'INTERNAL') {
                uncheckAllOptionInCategory(optionCode);
            }
        }

        setMaxOptionCategoryDateAndMaxDate(!checked);
        calculatePrice();

        insertOptionNames();
    });

    finalUsePointsElem.keyup(function() {
        var points = 1*removeComma($(this).val());
        var currentPoint = $(this).data('current-points');
        var maxPoints = (totalOptionPrice > currentPoint)?1*currentPoint:1*totalOptionPrice;
        if (points > maxPoints) {
            $('input[name=use-points]').val(maxPoints.toLocaleString());
        }
    });

    $('#btnUsePoint').click(function() {
        $.ajax({
            type: 'GET',
            url: '/users/me/points',
            success: function (response) {
                var points = response.data;
                if (points === 0) {
                    alert('누적된 포인트가 없습니다.');
                    return;
                }

                var remainPoints = parseInt($(finalUsePointsElem).data('current-points')) - parseInt(removeComma($(finalUsePointsElem).val()));
                $('#currentPointsTxt').text(remainPoints.toLocaleString() + ' P');
                usedPoints = 1*removeComma($(finalUsePointsElem).val());
                calculatePrice();
            },
            error: function() {
                alert('포인트 조회에 실패했습니다. 문제가 계속될 경우 고객센터로 문의주세요.');
            }
        });
    });

    $('#btnCancelUsePoint').click(function() {
        usedPoints = 0;
        $(finalUsePointsElem).val(0);
        $('#currentPointsTxt').text(parseInt($(finalUsePointsElem).data('current-points')).toLocaleString())
        calculatePrice();
    });

    // $(".modal").on('show.bs.modal', lock);
    $("#deposit_money_refund_info [type=checkbox]").click(lock);
    $("#deposit_money_refund_info .btn-primary").click(function() {
      $("#deposit_money_refund_info").submit();
    });

    $("#deposit_money_refund_info [type=checkbox]").click(lock);
    $("#deposit_money_refund_info .btn-primary").click(function() {
      $("#deposit_money_refund_info").submit();
    });

    init();
});

function lock() {
    var flag = $("#option-info [type=checkbox]").prop('checked');
    if (!flag) {
        $("#option-info .btn-primary").attr("disabled", "disabled");
    } else {
        $("#option-info .btn-primary").removeAttr("disabled");
    }
}

function syncPostingEndAt(dateformat) {
    $('.option-limit[data-option-code=INTERNAL]').text(dateformat);
    $('.option-limit[data-option-code=EXTERNAL]').text(dateformat);
}

function init() {
    if (mode && mode === 'EXTEND') {
        postingEndMoment = moment(postingEndAt);
    } else {
        postingEndMoment = moment().add(14, 'days');
        $('input[name=projectPaymentOptionId\\[\\]][data-checked=false]').prop('checked', false);
        $('.option-limit[data-option-code=INTERNAL]').text(postingEndMoment.format('YYYY.MM.DD'));
        $('.option-limit[data-option-code=EXTERNAL]').text(postingEndMoment.format('YYYY.MM.DD'));
        $('.option-limit[data-option-code=INTERNAL]').show();
        $('.option-limit[data-option-code=EXTERNAL]').show();
    }
}

function checkPrize() {
    var pickType = $('input[type=radio][name=pickType]:checked').val();
    var prizeFor1st = 1*removeComma(prizeFor1stElem.val());
    var prizeFor2nd = 1*removeComma(prizeFor2ndElem.val());
    var prizeFor3rd = 1*removeComma(prizeFor3rdElem.val());
    if (!prizeFor1st) {
        prizeFor1st = 0;
    }
    if (!prizeFor2nd) {
        prizeFor2nd = 0;
    }
    if (!prizeFor3rd) {
        prizeFor3rd = 0;
    }
    if (pickType === '3') {
        if ((prizeFor1st && prizeFor2nd && prizeFor3rd) && totalPrize != (prizeFor1st + prizeFor2nd + prizeFor3rd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if ((prizeFor3rd > prizeFor2nd) || (prizeFor3rd > prizeFor1st)) {
            $('.prize-input-container input').val('');
            alert('3위 상금은 1위 또는 2위상금보다 높을 수 없습니다.');
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위상금보다 높을 수 없습니다.');
            return false;
        }

        if (totalPrize == (prizeFor1st + prizeFor2nd + prizeFor3rd) && !prizeFor3rd && prizeFor2nd) {
            $('input[type=radio][name=pickType][value=2]').prop('checked', true);
            $('.container-2nd').show();
            $('.container-3rd').hide();
        } else if (totalPrize == (prizeFor1st + prizeFor2nd + prizeFor3rd) && !prizeFor3rd && !prizeFor2nd) {
            $('input[type=radio][name=pickType][value=1]').prop('checked', true);
            $('.container-2nd').hide();
            $('.container-3rd').hide();
        }
    } else if (pickType === '2') {
        if ((prizeFor1st && prizeFor2nd) && totalPrize != (prizeFor1st + prizeFor2nd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위 상금보다 높을 수 없습니다.');
            prizeFor1stElem.focus();
            return false;
        }

        if (totalPrize === (prizeFor1st + prizeFor2nd) && !prizeFor2nd) {
            $('input[type=radio][name=pickType][value=1]').prop('checked', true);
            $('.container-2nd').hide();
            $('.container-3rd').hide();
        }
    } else if (pickType === '1') {
        prizeFor1stElem.val(prizeFor1stElem);
        prizeFor1st = 1*removeComma(prizeFor1stElem.val());
        if (prizeFor1st === 0) {
            alert('금액을 입력해 주세요');
            return false;
        }
    }
    finalPrizeFor1stElem.html((1*removeComma(prizeFor1st)).toLocaleString());
    finalPrizeFor2ndElem.html((1*removeComma(prizeFor2nd)).toLocaleString());
    finalPrizeFor3rdElem.html((1*removeComma(prizeFor3rd)).toLocaleString());

    return true;
}

function setFee() {
    if (payForFeeToFreelancerYnElem.val() === 'true') {
        fee = Math.trunc(totalPrize * 0.1);
        $('.paying-fee-yn').text('예');
    } else {
        fee = 0;
        $('.paying-fee-yn').text('아니오');
    }
    $('.bill.fee').text(fee.toLocaleString());
}

function setPrize() {
    totalPrizeElem.html((removeComma(totalPrize)*1).toLocaleString());
    finalTotalPrizeElem.html((removeComma(totalPrize)*1).toLocaleString());
    // prizeFor1stElem.val(removeComma(totalPrize) * 0.6);
    // $(finalPrizeFor1stElem).html(removeComma(totalPrize) * 0.6);
    // prizeFor2ndElem.val(totalPrize * 0.3);
    // $(finalPrizeFor2ndElem).html(totalPrize * 0.3);
    // prizeFor3rdElem.val(totalPrize * 0.1);
    // $(finalPrizeFor3rdElem).val(totalPrize * 0.1);
    feeElem.html((totalPrize * 0.1).toLocaleString());

    finalIncentivePriceElem.html((1*removeComma(incentivePrizeElem.val())).toLocaleString());
}

function insertOptionNames() {
    var checked = $( "input[type='checkbox'][name=projectPaymentOptionId\\[\\]]:checked" );

    $(finalSelectedOptionsContainer).empty();
    for(var i=0; i<checked.length; i++){
        if ($(checked[i]).data('exclude-at-calculation')) continue;
        var name = $(checked[i]).data('name');
        var price = $(checked[i]).data('price');
        var optionLimit = $(checked[i]).closest('tr').find('.option-limit').text();
        if (!price) price = 0;
        $(finalSelectedOptionsContainer).append('<tr><th scope="row" class="tac">' + name + '</th><td class="tar">' + optionLimit +'</td><td>' + price.toLocaleString() + '원</td></tr>');
    }

    if (checked.length > 0) {
        $(finalSelectedOptionsContainer).show();
    }
}

function getCheckedChargedOptionCount() {
    var checked = $( "input[type='checkbox'][name=projectPaymentOptionId\\[\\]]:checked" );
    var optionCount = 0;
    for(var i=0; i<checked.length; i++) {
        var price = 1 * $(checked[i]).data('price');
        if (price <= 0) continue;
        optionCount++;
    }
    return optionCount;
}

function setMaxOptionCategoryDateAndMaxDate(trigeredByRemove) {
    var checked = $( "input[type='checkbox'][name=projectPaymentOptionId\\[\\]]:checked" );

    var maxDate = moment();
    var categoryMaxDateDic = {};

    for(var i=0; i<checked.length; i++){
        var price = 1*$(checked[i]).data('price');
        if (price <= 0) continue;
        var previousPurchased = $(checked[i]).data('previous-checked');
        var previousEndAt = $(checked[i]).data('previous-end-at');
        var category = $(checked[i]).data('option-category');
        var count = $(checked[i]).siblings('input[name=projectPaymentOptionCount\\[\\]]').val();
        var newEndAtMoment;
        if (previousPurchased) {
            newEndAtMoment = moment(previousEndAt).add(1*count, 'weeks');
        } else {
            newEndAtMoment = moment().add(1*count, 'weeks');
        }

        if (category && !categoryMaxDateDic[category]) {
            categoryMaxDateDic[category] = newEndAtMoment;
        }

        if (!category) {
            categoryMaxDateDic['ETC'] = newEndAtMoment;
        }

        if (newEndAtMoment.diff(categoryMaxDateDic[category]) > 0) {
            categoryMaxDateDic[category] = newEndAtMoment;
        }

        if (category && categoryMaxDateDic[category] && categoryMaxDateDic[category].diff(maxDate) > 0) {
            maxDate = categoryMaxDateDic[category];
        }
    }

    if (!categoryMaxDateDic['INTERNAL']) {
        categoryMaxDateDic['INTERNAL'] = moment().add(1, 'weeks');
    }

    var internalMaxDate = categoryMaxDateDic['INTERNAL'];
    var externalMaxDate = categoryMaxDateDic['EXTERNAL'];
    var etcMaxDate = categoryMaxDateDic['ETC'];

    optionMaxDate = internalMaxDate.startOf('day');
    if (externalMaxDate && externalMaxDate.diff(internalMaxDate) > 0) {
        optionMaxDate = externalMaxDate.startOf('day');
    }

    if (etcMaxDate && etcMaxDate.diff(optionMaxDate) > 0) {
        optionMaxDate = etcMaxDate.startOf('day');
    }

    if (postingEndMoment.startOf('day').diff(optionMaxDate.startOf('day')) < 0) {
        alert('선택하신 옵션상품의 만기일이 포스팅 기한보다 길어, 자동으로 포스팅 기간을 연장합니다.');
        postingEndMoment = optionMaxDate;
    } else if (trigeredByRemove) {
        postingEndMoment = optionMaxDate;
    }

    $('.option-limit[data-option-code=INTERNAL]').text(postingEndMoment.startOf('day').format('YYYY.MM.DD'));
    $('.option-limit[data-option-code=EXTERNAL]').text(postingEndMoment.startOf('day').format('YYYY.MM.DD'));

    postingEndAtChangeByCode = true;
    $('#postingEndAt').datepicker('setDate', postingEndMoment.startOf('day').format('YYYY-MM-DD'));
}

function uncheckAllOptionInCategory(category) {
    var checked = $( "input[type='checkbox'][data-option-category='" + category + "'][name=projectPaymentOptionId\\[\\]]:checked" );
    for(var i=0; i<checked.length; i++){
        $(checked[i]).prop('checked', false);
        var previousWeek = +$(this).closest('tr').find('.product-quantity').find(".data-count").data('count');

        var bill = $(checked[i]).closest('tr').find('.bill');
        var billVal = +removeComma(bill.text());
        var defaultVal = (billVal/previousWeek);
        var spanWeek=$(checked[i]).closest('tr').find('.product-quantity').find("i");
        spanWeek.text(1);

        bill.text(comma(defaultVal));
        $(checked[i]).data('price', defaultVal);

        var $countInput = $(checked[i]).siblings('input[name=projectPaymentOptionCount\\[\\]]');
        $countInput.val(1);
        $(checked[i]).closest('tr').find('.product-quantity').find(".data-count").data('count', 1);
        $(checked[i]).closest('tr').find('.product-quantity').find(".data-count").val(1);
        $(checked[i]).closest('tr').find('.product-limit').find('.option-limit').hide();
    }
}

function calculatePrice(){
    var chargedOptionIdMap = {};
    var sum=0;
    var checked = $( "input[type='checkbox'][name=projectPaymentOptionId\\[\\]]:checked" );

    var chargedOptionCount = 0;
    for(var i=0; i<checked.length; i++){
        if ($(checked[i]).data('exclude-at-calculation')) continue;
        var optionId = $(checked[i]).val();
        var price = $(checked[i]).data('price');
        var count = 1*$(checked[i]).siblings('input[name=projectPaymentOptionCount\\[\\]]').val();

        if (price > 0) {
            if (!$(checked[i]).data('previous-checked') || $(checked[i]).data('previous-checked') && $(checked[i]).data('option-added-triggered')) {
                chargedOptionCount += count;
                chargedOptionIdMap[optionId] = true
            }
        }
        if (!$(checked[i]).data('previous-checked') || $(checked[i]).data('previous-checked') && $(checked[i]).data('option-added-triggered')) {
            sum += price;
        }
    }
    $('#selectedOptionCount').text(chargedOptionCount.toLocaleString());
    var discountAmount = 0;

    if (chargedOptionCount === 2) {
        discountAmount = sum * 0.1;
    } else if (chargedOptionCount === 3) {
        discountAmount = sum * 0.15;
    } else if (chargedOptionCount >= 4) {
        discountAmount = sum * 0.20;
    }

    discountAmount = parseInt(discountAmount);

    $('.optionDiscountAmount').text(discountAmount.toLocaleString());

    totalOptionPrice = sum - discountAmount;

    $(".totalPrize").text(comma(totalPrize));
    $(".totalPostingOptionWhPoints").text(comma(totalOptionPrice - usedPoints));

    var supplyAmount = Math.trunc(totalOptionPrice - usedPoints + totalPrize + depositMoeny  + fee);
    if ('EXTEND' === mode) {
        supplyAmount = Math.trunc(totalOptionPrice - usedPoints);
    }
    var totalVat = Math.trunc(supplyAmount*0.1);


    finalSupplyPriceElem.html(supplyAmount.toLocaleString());
    finalVatElem.html(totalVat.toLocaleString());
    totalChargedPriceElem.html((supplyAmount + totalVat).toLocaleString());
    $('input[name=totalDiscountOptionPrice]').val(discountAmount);
    $('input[name=supplyAmountOfMoney]').val(supplyAmount);
    $('input[name=vatAmountOfMoney]').val(totalVat);
    $('input[name=totalAmountOfMoney]').val(supplyAmount + totalVat);
    $('input[name=optionAmountOfMoney]').val(sum);
}

function checkPrizeInput() {
    var pickType = $('input[type=radio][name=pickType]:checked').val();

    var prizeFor1st = 1*removeComma(prizeFor1stElem.val());
    var prizeFor2nd = 1*removeComma(prizeFor2ndElem.val());
    var prizeFor3rd = 1*removeComma(prizeFor3rdElem.val());

    if (pickType === '3') {
        if (totalPrize !== (prizeFor1st + prizeFor2nd + prizeFor3rd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if (prizeFor3rd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('3위 상금은 1위 또는 2위상금보다 높을 수 없습니다.');
            return false;
        }

    } else if (pickType === '2') {
        if (totalPrize !== (prizeFor1st + prizeFor2nd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위 상금보다 높을 수 없습니다.');
            prizeFor1stElem.focus();
            return false;
        }
    } else if (pickType === '1') {
        prizeFor1stElem.val(totalPrize);
        prizeFor1st = 1*removeComma(prizeFor1stElem.val());
        if (prizeFor1st === 0) {
            alert('금액을 입력해 주세요');
            return false;
        }
    }

    return true;
}

function validateStep(stepIndex) {
    if (stepIndex === 1) {
        var checkedValue = ('true' === $('input:radio[name=incentive]:checked').val());
        if (checkedValue && (!checkedValue || 1*$('input[name=incentivePrize]').val() === 0)) {
            alert('인센티브 계획이 있으시면 금액을 입력해주세요. 계획이 없으시면 아니오를 선택해주세요.');
            return false;
        }
    }

    if (stepIndex === 2 && !$('input[name=pickType]:checked').val()) {
        alert('순위 옵션을 선택해 주세요.');
        return false;
    }

    if (stepIndex === 2 && !checkPrizeInput()) {
        return false;
    }

    if (stepIndex === 2) {
        calculatePrice();
    }

    if (stepIndex === 4) {
        insertOptionNames();
        usedPoints = 0;
        $(finalUsePointsElem).val(0);
        setFee();
        calculatePrice();
    }

    console.log(stepIndex);

    return true;
}

