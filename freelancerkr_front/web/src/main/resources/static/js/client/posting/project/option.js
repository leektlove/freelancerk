var optionMaxDate;
var optionIdAddAgreeDic = {};

$(document).ready(function() {
    $( "#preview_pop" ).on('shown.bs.modal', function(){
        if ($('input[name=projectPaymentOptionId\\[\\]][data-option-code="ESCROW"]').is(':checked')) {
            $('[data-product-item-code=ESCROW]').show();
        } else {
            $('[data-product-item-code=ESCROW]').hide();
        }

        if ($('input[name=projectPaymentOptionId\\[\\]][data-option-code="INTERNAL_URGENT"]').is(':checked')
            || $('input[name=projectPaymentOptionId\\[\\]][data-option-code="EXTERNAL_URGENT"]').is(':checked')) {
            $('[data-product-item-code=URGENT]').show();
        } else {
            $('[data-product-item-code=URGENT]').hide();
        }

        $('#dDay').text(postingEndMoment.diff(moment(), 'days') + 1);
    });
    $('#btnPay').click(function () {
        if(!confirm('결제를 계속 진행하시겠습니까?')) {
            return;
        }

        var optionAmountOfMoney = parseInt($('input[name=optionAmountOfMoney]').val());
        if ('EXTEND' === mode && (!optionAmountOfMoney || optionAmountOfMoney === 0)) {
            alert('결제금액이 없으므로 이전 페이지로 이동합니다. ');
            location.href = '/client/payment/list';
            return;
        }

        if ('EXTEND' !== mode && optionAmountOfMoney === 0) {
            $('#btnStartWithoutPurchase').trigger('click');
            return;
        }

        if (optionMaxDate && optionMaxDate.diff(postingEndMoment) > 0 ) {
            if (confirm('선택하신 옵션의 만료일이 포스팅의 만료일보다 깁니다. 포스팅 만료일을 옵션의 만료일까지 연장하시겠습까?')) {
                $('input[name=postingPeriodExtend]').val(true);
            } else {
                $('input[name=postingPeriodExtend]').val(false);
            }
        }

        var checkedOption = $('input[name=projectPaymentOptionId\\[\\]]');

        for(var i=0; i<checkedOption.length; i++){
            var option = checkedOption[i];
            if (!option.checked) {
                $(option).siblings('input[name=projectPaymentOptionCount\\[\\]]').remove();
                $(option).siblings('input[name=projectPaymentOptionIncludedInPack\\[\\]]').remove();
                try {
                    option.remove();
                } catch (e) {
                    console.error(e);
                }
            }
        }

        if ('EXTEND' === mode) {
            $('input[name=projectPaymentOptionId\\[\\]]').each(function(index, item) {
                var checked = $(item).is(':checked');
                if (checked) {
                    var previousChecked = $(item).data('previous-checked');
                    var optionAddTriggered = $(item).data('option-added-triggered');

                    if (previousChecked && !optionAddTriggered) {
                        $(item).siblings('input[name=projectPaymentOptionCount\\[\\]]').remove();
                        $(item).remove();
                    }
                }
            });
        }

        location.href = '/client/posting/projectPayment?projectId=' + projectId + '&' + $('#foption').serialize();
    });

    $('input[name=projectPaymentOptionId\\[\\]]').change(function() {
        var checked = $(this).is(':checked');
        var previousChecked = $(this).data('previous-checked');
        if (previousChecked) {
            $(this).prop('checked', true);
            return;
        }

        if (checked) {

            $(this).closest('tr').find('.product-limit ').find('.option-limit').show();
            var price = 1*$(this).data('price');
            if (price > 0) {
                var count = $(this).closest('tr').find('.product-quantity').find('.data-count').data('count');
                $(this).closest('tr').find('.product-limit ').find('.option-limit').text(moment().add(count, 'weeks').format('YYYY.MM.DD'));

                $($(this).closest('tr').find('input[name=projectPaymentOptionId\\[\\]]')[0]).data('price', price*1);
                $(this).closest('tr').find('input[name=projectPaymentOptionCount\\[\\]]').val(1);
                $(this).closest('.product-quantity').siblings('.product-limit ').find('.option-limit').text(moment().add(1, 'weeks').format('YYYY.MM.DD'));

            } else {
                var internalOptionLimit = $('.option-limit[data-option-code=INTERNAL]').text();
                $(this).closest('tr').find('.product-limit ').find('.option-limit').text(internalOptionLimit);
            }
        } else {
            var price = $(this).data('price');
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
            var previousWeek = +$(this).closest('tr').find('.product-quantity').find(".data-count").data('count');

            if (!isNaN(previousWeek)) {
                var bill = $(this).closest('tr').find('.bill');
                var billVal = +removeComma(bill.text());
                var defaultVal = (billVal / previousWeek);
                var spanWeek = $(this).closest('tr').find('.product-quantity').find("i");
                spanWeek.text(1);

                bill.text(comma(defaultVal));
                $(this).data('price', defaultVal);

                var $countInput = $(this).siblings('input[name=projectPaymentOptionCount\\[\\]]');
                $countInput.val(1);
                $(this).closest('tr').find('.product-quantity').find(".data-count").data('count', 1);
                $(this).closest('tr').find('.product-quantity').find(".data-count").val(1);
                $(this).closest('tr').find('.product-limit').find('.option-limit').hide();
            }

            if (optionCode === 'EXTERNAL' || optionCode === 'INTERNAL') {
                uncheckAllOptionInCategory(optionCode);
            }
        }

        setMaxOptionCategoryDateAndMaxDate(!checked);
        calculatePrice();
    });

    $('#btnStartWithoutPurchase').click(function() {
       if(!confirm('포스팅 옵션을 사용하시면 좀 더 많은 견적을 받아보실 수 있습니다. 이대로 프로젝트를 등록하시겠습니까?')) {
           return;
       }

       var externalExpose = $('input[data-option-code="EXTERNAL"]').is(':checked');
       var useEscrow = $('input[data-option-code="ESCROW"]').is(':checked');
       var postingEndAt = $('input[name=postingEndAt]').val();

       $.ajax({
           type: 'PUT',
           url: '/projects/' + projectId + '?status=POSTED&externalExpose=' + externalExpose + '&useEscrow=' + useEscrow + '&postingEndAt=' + postingEndAt,
           success: function(data, textStatus, jqXHR) {
               location.href = '/view/pick-me-ups?afterPosting=true&category1stId=' + projectFirstTopCategoryId;
           },
           error: function(jqXHR, textStatus, errorThrown) {
               alert('요청 중 문제가 발생했습니다.' + jqXHR.data);
           }
       })
    });

    $( ".project-container .plus" ).click(function() {
        var checked = $(this).closest('tr').find('input[type=checkbox]').is(':checked');
        if (!checked) {
            alert('옵션을 먼저 선택해주세요.');
            return;
        }

        var previousPurchased = $(this).closest('tr').find('input[type=checkbox]').data('previous-checked');
        var productId = $(this).closest('tr').find('input[type=checkbox]').val();

        if (!optionIdAddAgreeDic[productId] && previousPurchased) {
            if(!confirm('기존에 구매하신 옵션입니다. 연장을 원하시면 확인 버튼을 클릭해주세요')) {
                return;
            }
            optionIdAddAgreeDic[productId] = true;
        }

        var optionAddTriggered = $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered');

        var week = +$(this).siblings(".data-count").data('count');
        var bill = $(this).closest('tr').find('.bill');
        var billVal = +uncomma(bill.text());

        if (week >= 52) {
            alert("기간설정은 1주 부터 52주 까지 가능합니다.");
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

    });

    $( ".project-container .minus" ).click(function() {
        var checked = $(this).closest('tr').find('input[type=checkbox]').is(':checked');
        if (!checked) {
            alert('옵션을 먼저 선택해주세요.');
            return;
        }

        var week = +$(this).siblings(".data-count").data('count');
        var bill = $(this).closest('tr').find('.bill');
        var billVal = +uncomma(bill.text());
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
                setMaxOptionCategoryDateAndMaxDate(false);
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
        }
    });

    init();
});

function init() {
    if ('EXTEND' !== mode) {
        $('input[name=projectPaymentOptionId\\[\\]][data-checked=false]').prop('checked', false);
        $('.option-limit[data-option-code=ESCROW]').text(postingEndAt);
        $('.option-limit[data-option-code=INTERNAL]').text(postingEndAt);
        $('.option-limit[data-option-code=EXTERNAL]').text(postingEndAt);
    }
}

function uncheckAllOptionInCategory(category) {
    var checked = $( "input[type='checkbox'][data-option-category='" + category + "'][name=projectPaymentOptionId\\[\\]]:checked" );
    for(var i=0; i<checked.length; i++){
        $(checked[i]).prop('checked', false);
        var previousWeek = +$(checked[i]).closest('tr').find('.product-quantity').find(".data-count").data('count');

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

function changePackageItemOptionCheckedStatus(arr, checked) {
    for (var i = 0; i < arr.length; i++) {
        var code = arr[i];
        if (code.endsWith('_PACK')) {
            var containItemCodesInPack = $('[data-option-code=' + code + ']').data('contain-item-codes');
            if (containItemCodesInPack) {
                changePackageItemOptionCheckedStatus(containItemCodesInPack.split(','), checked);
            }
        }
        if (!code.endsWith('_PACK')) {
            $('input[data-option-code="' + code + '"]').prop('checked', checked);
            $('input[data-option-code="' + code + '"]').data('exclude-at-calculation', checked);
            $('input[data-option-code="' + code + '"]').siblings('input[name=projectPaymentOptionIncludedInPack\\[\\]]').val(checked);
        }
    }
}

function uncomma(str) {
    str = String(str);
    return str.replace(/[^\d]+/g, '');
}

function setMaxOptionCategoryDateAndMaxDate(trigeredByRemove) {
    var checked = $( "input[type='checkbox'][name=projectPaymentOptionId\\[\\]]:checked" );

    var maxDate = moment(postingEndAt);
    var categoryMaxDateDic = {};

    for(var i=0; i<checked.length; i++){
        var price = 1*$(checked[i]).data('price');
        if (price <= 0) continue;
        var category = $(checked[i]).data('option-category');
        var previousPurchased = $(checked[i]).data('previous-checked');
        var previousEndAt = $(checked[i]).data('previous-end-at');
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
        categoryMaxDateDic['INTERNAL'] = moment(postingEndAt);
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

    $('.option-limit[data-option-code=INTERNAL]').text(postingEndMoment.format('YYYY.MM.DD'));
    $('.option-limit[data-option-code=ESCROW]').text(postingEndMoment.format('YYYY.MM.DD'));
    $('.option-limit[data-option-code=EXTERNAL]').text(postingEndMoment.format('YYYY.MM.DD'));

    $('input[name=postingEndAt]').val(postingEndMoment.format('YYYY-MM-DD'));
}

function calculatePrice(){
    var chargedOptionIdMap = {};
    var sum=0;
    var checked = $( "input[type='checkbox']:checked" );

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

    $('input[name=optionAmountOfMoney]').val(sum);
    $('.optionDiscountAmount').text(discountAmount.toLocaleString());
    $("#totalAmount").text(comma(sum - discountAmount));
}