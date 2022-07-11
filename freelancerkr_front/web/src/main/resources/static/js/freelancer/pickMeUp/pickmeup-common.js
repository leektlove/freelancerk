var maxOptionCount = 1;
var optionMaxDate;
var optionIdAddAgreeDic = {};

$(document).ready(function () {

    init();

    var pickMeUpChecked = $('input[name=productItem][data-product-code=PICK_ME_UP]').is(":checked");

    // 대분류 변화에 따른 소분류 변화
    $('#selectMainCategory').change(function (e) {
        var mainCategoryId = $('#selectMainCategory option:selected').val();
        $("#inputCategory1st").val($('#selectMainCategory option:selected').text());

        // console.log("pC : ", mainCategoryId);
        $("#selectSubCategory").find('option')
            .remove()
            .end()
            .append('<option th:value="null">선택</option>');

        $.ajax({
            method: 'GET',
            url: '/categories?parentCategoryId=' + mainCategoryId,
            cache: false,
            contentType: false,
            processData: false,
            success: function (result) {
                for (var i = 0; i < result.length; i++) {
                    // console.log(result[i]["name"], " ", result[i]["id"])
                    $("#selectSubCategory").append(new Option(result[i]["name"], result[i]["id"]));
                }
            },
            error: function (err, textStatus, xhr) {
                console.log(err);
            }
        });
    });

    $('#selectSubCategory').change(function (e) {
        $("#inputCategory2nd").val($('#selectSubCategory option:selected').text());
    });

    $('.disableLayer').click(function(e) {
        alert(' 먼저 [픽미업 공개] 옵션을 선택해주세요.');
    });

    /**
     * 동적 변경
     */
    // UI 처리 (by PEZ)
    $(".popup-btn").click(function () {
        $(".popup-bg").css("display", "block");
        $(".option-popup").css("display", "block");
    });

    $(".popup-close").click(function () {
        $(".popup-bg").css("display", "none");
        $(".option-popup").css("display", "none");
    });

    $('input[name=portfolioMainPic]').on('change', function() {
        if (!checkImageType(this.files[0])) {
            alert('jpg, png, gif 형식의 파일을 업로드해주세요.(5M제한)');
            $(this).val('');
            return;
        }

        if (!checkImageSize(this.files[0])) {
            alert('파일용량은 5M로 제한됩니다. 다시 업로드해주세요.');
            $(this).val('');
            return;
        }


    });

    $('body').on('change', 'input[name=pickMeUpSubPics]', function() {
        if (!checkImageType(this.files[0])) {
            alert('jpg, png, gif 형식의 파일을 업로드해주세요.(5M제한)');
            $(this).val('');
            return;
        }

        if (!checkImageSize(this.files[0])) {
            alert('파일용량은 5M로 제한됩니다. 다시 업로드해주세요.');
            $(this).val('');
            return;
        }

        var imageSelector = $(this).siblings('div').find('img')[0];
        originInputSelector = $(this);
        hiddenInputSelector = $(this).siblings('input[name=subImagesUrl\\[\\]]');

        var formDataForOriginal = new FormData();
        formDataForOriginal.append('file', originInputSelector[0].files[0]);

        $.ajax('/files', {
            method: 'POST',
            data: formDataForOriginal,
            processData: false,
            contentType: false,
            success: function (res) {
                $(hiddenInputSelector).val(res);
                $(imageSelector).attr('src', res);
            },

            error: function () {
                alert('이미지 저장에 실패했습니다.');
            },

            complete: function () {
            },
        });

        $(this).hide();
    });

    $('input[name=portfolioMainPic]').on('click', function() {
        $(this).val('');
    });

    $('input[name=productItem]').change(function() {
        var productCode = $(this).data('product-code');
        var checked = $(this).is(':checked');

        if ($(this).data('valid-ticket')) {
            return false;
            // if(!confirm('[주의] 아직 기한이 남아있는 옵션입니다. 선택박스를 해제하시면 해당 옵션의 기능은 적용되지 않으며, 잔여 기간에 관계없이 재이용 또는 환불이 불가능합니다. 계속 진행하시겠습니까?')) {
            //     $(this).prop('checked', true);
            //     return false;
            // } else {
            //     $(this).closest('tr').find('.option-limit ').text('');
            //     if ('PICK_ME_UP' === productCode) {
            //         $(this).closest('table').find('.option-limit ').text('');
            //     }
            //     return true;
            // }
        }

        if (!checked) {
            $(this).closest('tr').find('.option-limit').hide();
            $(this).closest('tr').find('input[name=count]').val(0);
            $(this).closest('tr').find('input[name=inputProductMonth]').val(1);
            var unitPrice = parseInt($(this).closest('tr').find('input[name=inputProductUnitPrice]').val());
            $(this).closest('tr').find('input[name=inputProductMoney]').val(unitPrice.toLocaleString());

            setMaxOptionDate();

        } else {
            $(this).closest('tr').find('.option-limit').show();
            if ('PICK_ME_UP' === productCode && remainFreeChargeProductCount > 0) {
                $(this).closest('tr').find('.option-limit ').text('무제한');
            } else {
                $(this).closest('tr').find('.option-limit ').text(moment().add(1, 'months').format('YYYY.MM.DD'));
            }
            $(this).closest('tr').find('input[name=count]').val(1);
            setMaxOptionDate();
        }

        if ('DIRECT_DEAL' === productCode && $(this).is(':checked')) {
            var exposedContactsHtml = '';
            if (exposeSns) {
                exposedContactsHtml += '<span class="text-secondary">SNS</span>';
            }
            if (exposeCellphone) {
                if (exposedContactsHtml) {
                    exposedContactsHtml += ', ';
                }
                exposedContactsHtml += '<span class="text-secondary">전화번호</span>';
            }
            if (exposeEmail) {
                if (exposedContactsHtml) {
                    exposedContactsHtml += ', ';
                }
                exposedContactsHtml += '<span class="text-secondary">이메일</span>';
            }
            // $('.contact-available-time-pick-container').show();
            $('#exposedContactsContainer').html(exposedContactsHtml);
            $('#directMarket').modal('show');
        } else {
            // $('.contact-available-time-pick-container').hide();
        }

        if ('PICK_ME_UP' === productCode) {
            if (checked && remainFreeChargeProductCount > 0) {
                $('#remainFreeChargeProductCount').text(remainFreeChargeProductCount - 1);
            } else if (!checked && remainFreeChargeProductCount > 0) {
                $('#remainFreeChargeProductCount').text(remainFreeChargeProductCount);
            }
        }
    });

    // 상품 갯수
    $(".btnIncreaseMonth").click(function (e) {
        var isDisabled = $(this).attr("disabled");
        if (isDisabled === "disabled") return;

        var isChecked = $(this).closest('tr').find(".checkboxProductItem").is(":checked");
        if (isChecked !== true) {
            alert('옵션을 선택해 주세요');
            return;
        }

        var previousPurchased = $(this).closest('tr').find('input[type=checkbox]').data('valid-ticket');
        var productId = $(this).closest('tr').find('input[type=checkbox]').data('product-id');
        if (!optionIdAddAgreeDic[productId] && previousPurchased) {
            if(!confirm('기존에 구매하신 옵션입니다. 연장을 원하시면 확인 버튼을 클릭해주세요')) {
                return;
            }
            optionIdAddAgreeDic[productId] = true;
        }

        var optionAddTriggered = $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered');

        var month = parseInt($(this).siblings('input[name=count]').val());
        month += 1;
        var monthTxt = month;

        $(this).siblings('input[name=count]').val(month);

        if (previousPurchased && !optionAddTriggered) {
            $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered', true);
            monthTxt -= 1;
        }

        if (monthTxt === 0) {
            monthTxt = 1;
        }

        $(this).siblings('input[name=inputProductMonth]').val(monthTxt);

        var unitPrice = parseInt($(this).closest('tr').find(".inputProductUnitPrice").val());
        $(this).closest('tr').find(".inputProductMoney").val((unitPrice*monthTxt).toLocaleString());

        if (previousPurchased) {
            var previousEndAt = $(this).closest('tr').find('input[type=checkbox]').data('previous-end-at');
            $(this).closest('tr').find('.option-limit ').text(moment(previousEndAt).add(month, 'months').format('YYYY.MM.DD'));
        } else {
            $(this).closest('tr').find('.option-limit ').text(moment().add(month, 'months').format('YYYY.MM.DD'));
        }

        setMaxOptionDate();
        calculateSumOfProductMoney();
    });

    $(".btnDecreaseMonth").click(function (e) {
        var isDisabled = $(this).attr("disabled");
        if (isDisabled === "disabled") return;

        var isChecked = $(this).closest('tr').find(".checkboxProductItem").is(":checked");
        if (isChecked !== true) {
            alert('옵션을 선택해 주세요');
            return;
        }

        var month = parseInt($(this).siblings('input[name=count]').val());

        var productId = $(this).closest('td').siblings('.product-name').find('input[type=checkbox]').val();
        var previousPurchased = $(this).closest('tr').find('input[type=checkbox]').data('valid-ticket');
        var optionAddTriggered = $(this).closest('tr').find('input[type=checkbox]').data('option-added-triggered');

        if (!previousPurchased) {
            if (month <= 1) {
                alert("기간설정은 1개월 이상이어야 합니다.");
                return;
            }
        } else {
            if (optionAddTriggered && previousPurchased && month === 1) {
                $(this).siblings('input[name=inputProductMonth]').val(1);
                $(this).siblings('input[name=count]').val(0);
                var previousEndAt = $(this).closest('tr').find('input[type=checkbox]').data('previous-end-at');
                var unitPrice = parseInt($(this).closest('tr').find(".inputProductUnitPrice").val());
                $(this).closest('tr').find(".inputProductMoney").val(unitPrice.toLocaleString());

                optionIdAddAgreeDic[productId] = false;

                $(this).closest('tr').find('.option-limit ').text(moment(previousEndAt).format('YYYY.MM.DD'));
                setMaxOptionDate();
                calculateSumOfProductMoney();
            } else if (month === 1) {
                alert('기간설정은 1개월 이상이어야 합니다.');
                return;
            }
        }
        if((!previousPurchased && month <= 1) || (optionAddTriggered && previousPurchased && month <= 0)){
            alert("기간설정은 1개월 이상이어야 합니다.");
            return;
        } else {
            month -= 1;
            var monthTxt = month;
            if (monthTxt === 0) {
                monthTxt = 1;
            }
            $(this).siblings('input[name=inputProductMonth]').val(monthTxt);
            $(this).siblings('input[name=count]').val(month);

            var unitPrice = parseInt($(this).closest('tr').find(".inputProductUnitPrice").val());
            $(this).closest('tr').find(".inputProductMoney").val((unitPrice*monthTxt).toLocaleString());

            if (previousPurchased) {
                var previousEndAt = $(this).closest('tr').find('input[type=checkbox]').data('previous-end-at');
                $(this).closest('tr').find('.option-limit ').text(moment(previousEndAt).add(month, 'months').format('YYYY.MM.DD'));
            } else {
                $(this).closest('tr').find('.option-limit ').text(moment().add(month, 'months').format('YYYY.MM.DD'));
            }

            setMaxOptionDate();
            calculateSumOfProductMoney();
        }
    });

    $(".checkboxProductItem").change(function (e) {
        var isCheck = e.target.checked;
        // console.log("isChecked : ", isCheck);

        if (!isCheck && $(this).data('valid-ticket')) {
            $(this).prop('checked', true);
            return false;
        }
        // 기본 옵션일 경우
        var index = $(this).attr('id');
        var type = $(this).data('type');
        if (type === 'STANDARD') {
            $(".checkboxProductItem").each(function (index, element) {
                // console.log("index : ", index);
                var type = $(element).data('type');
                if (index > 0 && type !== 'PACKAGE') {
                    if (isCheck === true) {
                        $(element).removeAttr("disabled");
                        $(element).siblings('.disableLayer').css('width', 0);
                        $(element).siblings('.disableLayer').css('height', 0);
                        $(element).parent().parent().find(".btnDecreaseMonth").removeAttr("disabled");
                        $(element).parent().parent().find(".btnIncreaseMonth").removeAttr("disabled");
                    } else {
                        $(element).attr("disabled", true);
                        $(element).siblings('.disableLayer').css('width', '20px');
                        $(element).siblings('.disableLayer').css('height', '20px');
                        $(element).prop("checked", false);
                        $(element).parent().parent().find(".btnDecreaseMonth").attr("disabled", true);
                        $(element).parent().parent().find(".btnIncreaseMonth").attr("disabled", true);
                    }
                }
            });
        } else if ('BASIC' === type) {
            var selectedProductIds = $("input.checkboxProductItem:checked").map(function() {
                return $(this).data('product-id');
            }).get().join(",");
        }
        if (isCheck) {
            var month = $(this).closest('tr').find('input[name=inputProductMonth]').val();
            var productCode = $(this).data('product-code');
            if ('PICK_ME_UP' === productCode && remainFreeChargeProductCount > 0) {
                $(this).closest('tr').find('.option-limit ').text('무제한');
            } else {
                $(this).closest('tr').find('.option-limit ').text(moment().add(month, 'months').format('YYYY.MM.DD'));
            }
            setMaxOptionDate();
        }
        calculateSumOfProductMoney();
    });

    $('select[name=workPlace]').on('change', function() {
        var value = $(this).val();
        if ('OFF_LINE' === value) {
            $('.meet-up-place-container').show();
        } else {
            $('.meet-up-place-container').hide();
        }
    });
});

function init() {
    var meetUpPlace = $('select[name=workPlace] option:selected').val();
    if ('OFF_LINE' === meetUpPlace) {
        $('.meet-up-place-container').show();
    } else {
        $('.meet-up-place-container').hide();
    }

    if (contentType && contentType === 'BLOG') {
        if ($('#summernote').length > 0 ) {
            $('#summernote').summernote({
                lang: 'ko-KR',
                height: 500,
                // placeholder: 'write here...',
                fontNames: ['Nanum Gothic', 'Apple SD Gothic Neo', 'sans-serif'],
                toolbar: [
                    // [groupName, [list of button]]
                    ['style', ['bold', 'italic', 'strikethrough', 'underline']],
                    ['fontname', ['Nanum Gothic', 'Apple SD Gothic Neo', 'sans-serif']],
                    ['fontsize', ['fontsize']],
                    ['color', ['color']],
                    ['para', ['paragraph']],
                    ['height', ['height']],
                    ['insert', ['link', 'picture', 'video']],
                    ['table', ['table']],
                    ['hr', ['hr']],
                    ['view', ['undo', 'redo']],
                ],
                callbacks: {
                    onImageUpload: function (files, editor, welEditable) {
                        if ($('.note-editable img').length >= 5) {
                            alert('이미지는 5장 까지 첨부 가능합니다.');
                            return;
                        }
                        for (var i = files.length - 1; i >= 0; i--) {
                            sendFile(files[i], this);
                        }
                    }
                }
            });
            $('#summernote').summernote('code', $('textarea[name=description]').val());
        }
    }
}

function sendFile(file, el) {
    var form_data = new FormData();
    form_data.append('file', file);
    $.ajax({
        data: form_data,
        type: "POST",
        url: '/files',
        cache: false,
        contentType: false,
        enctype: 'multipart/form-data',
        processData: false,
        success: function(img_name) {
            $(el).summernote('editor.insertImage', img_name);
        }
    });
}

function getMaxOptionCount() {
    var maxOptionCount = 0;
    $(".checkboxProductItem").each(function (index, element) {
        var topParentElement = $(element).closest('tr');

        var isDisabled = $(element).attr("disabled");
        var isChecked = $(element).is(":checked");
        var excludeCalculation = $(element).data('exclude-at-calculation');

        // console.log("index : ", index, ", isDisabled : ", isDisabled, ", isChecked : ", isChecked);

        if (index === 0 && isDisabled === "disabled" || excludeCalculation) {
            return;
        }

        if (isDisabled === undefined && isChecked === true) {
            var money = topParentElement.data('unit-price');
            var count = $(this).closest('tr').find('input[name=inputProductMonth]').val();

            if (isNaN(count)) {
                count = 0;
            }

            if (isNaN(money)) {
                money = 0;
            }

            if (maxOptionCount < count) {
                maxOptionCount = count;
            }
        }
    });

    return maxOptionCount;
}
/**
 * Helper Functions
 */
// 총합 계산
function calculateSumOfProductMoney() {
    var sum = 0;
    var chargedOptionCount = 0;

    $(".checkboxProductItem").each(function (index, element) {
        var topParentElement = $(element).closest('tr');

        var isDisabled = $(element).attr("disabled");
        var isChecked = $(element).is(":checked");
        var excludeCalculation = $(element).data('exclude-at-calculation');

        // console.log("index : ", index, ", isDisabled : ", isDisabled, ", isChecked : ", isChecked);

        if (index === 0 && isDisabled === "disabled" || excludeCalculation) {
            return;
        }

        if (isDisabled === undefined && isChecked === true) {
            var money = topParentElement.data('unit-price');
            var count = parseInt($(this).closest('tr').find('input[name=count]').val());

            if (isNaN(count)) {
                count = 0;
            }

            if (isNaN(money)) {
                money = 0;
            }

            if (topParentElement.attr('class') === 'standardProduct' && count <= remainFreeChargeProductCount) {
                // $('.remainFreeChargeProductCount.on').show();
                // $('.remainFreeChargeProductCount.off').hide();
                // $('#remainFreeChargeProductCount').text(remainFreeChargeProductCount - count);
            } else if (topParentElement.attr('class') === 'standardProduct' && count > remainFreeChargeProductCount) {
                sum = sum + (count-remainFreeChargeProductCount)*parseInt(money);
                chargedOptionCount += count;
                // $('.remainFreeChargeProductCount.on').hide();
                // $('.remainFreeChargeProductCount.off').show();
                // $('#remainFreeChargeProductCount').text(0);
            } else {
                sum = sum + count*parseInt(money);
                chargedOptionCount += count;
            }
        }
    });

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

    $('#optionDiscountAmount').text(discountAmount.toLocaleString());
    $("#sumOfProductMoney").val(comma(sum - discountAmount));
}

function checkAllContains(selectedOptionSet, packItems) {
    var size = packItems.size;
    var hit = 0;
    for (var i in Array.from(packItems)) {
        if(selectedOptionSet.has(Array.from(packItems)[i])) {
            hit++;
        }
    }

    if (size === hit) {
        $('#basicProduct input').prop('checked', false);
        alert('동일한 옵션이 패키지옵션에 있습니다.');
        return true;
    }
    return false;
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

$(document).on('click', '.btn-portfolio-details', function() {
   var linkUrl = $(this).attr('href');
   location.href = linkUrl;
});

function deletePickMeUp(elem, id, expose, freeCharge) {
    if (freeCharge && !confirm('해당 포트폴리오는 기한에 제한이 없는(무제한) [픽미업 공개] 옵션이 적용중입니다. 삭제하시면 옵션은 복구되지 않습니다. 정말 삭제하시겠습니까? ')) {
        return;
    }
    if (!confirm('삭제하시면 해당 포트폴리오의 옵션 결제내역도 함께 삭제됩니다. 따라서 결제내역이 있는 경우, 삭제보다는 수정을 권장합니다. 정말 삭제하시겠습니까? ')) {
        return;
    }
    $.ajax({
        type: 'DELETE',
        url: '/pick-me-ups/' + id,
        success: function() {
            location.reload();
        },
        error: function() {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의주세요.');
        }
    })
}

function toRegisterPortfolio() {
    if (!userInfoInput) {
        alert('프로필을 먼저 입력하신 후 포트폴리오를 등록해주세요.');
        location.href='/freelancer/profile/modify';
        return;
    }
    // if (totalPortfolioCount >= 10) {
    //     alert('포트폴리오 등록은 10개까지 가능합니다. 기존의 포트폴리오를 수정하시거나 삭제 후 이용해주세요.');
    //     return;
    // }
    location.href='/freelancer/pickMeUp/choiceEnrollment';
}



function countChar(elem) {
    var content = $(elem).val();
    var len = content.length;
    if (len >= 1000) {
        $('textarea[name=description]').val(content.substring(0, 1000));
    } else {
        $('#textCnt').text(len.toLocaleString());
    }
}

function countCharInput(elem) {
    var content = $(elem).val();
    var len = content.length;
    if (len >= 20) {
        $('input[name=projectName]').val(content.substring(0, 20));
    } else {
        $('#cntName').text(len.toLocaleString());
    }
}

function toProfile() {
    if (!confirm('프로필 수정으로 이동하시겠습니까?')) {
        return;
    }
    location.href = '/freelancer/profile/modify';
}


function setMaxOptionDate() {
    var checked = $( "input[type='checkbox'][name=productItem]:checked" );

    var maxDate = moment();
    var maxCount = 0;

    for(var i=0; i<checked.length; i++){
        var count = $(checked[i]).closest('tr').find('input[name=count]').val();
        var previousPurchased = $(checked[i]).data('valid-ticket');
        var previousEndAt = $(checked[i]).data('previous-end-at');

        var newEndAtMoment;
        if (previousPurchased) {
            newEndAtMoment = moment(previousEndAt).add(1*count, 'months');
        } else {
            newEndAtMoment = moment().add(1*count, 'months');
        }

        if (newEndAtMoment.diff(maxDate) > 0) {
            maxDate = newEndAtMoment;
        }

        if (maxCount < count) {
            maxCount = count;
        }
    }

    if (remainFreeChargeProductCount === 0 ) {
        $('input[name=inputProductMonth][data-product-code=PICK_ME_UP]').val(maxCount);
    }

    if ($('input[name=productItem][data-product-code=PICK_ME_UP]').is(':checked') && remainFreeChargeProductCount > 0) {
        $('.option-limit[data-option-code=PICK_ME_UP]').text('무제한');
    } else {
        $('.option-limit[data-option-code=PICK_ME_UP]').text(moment(maxDate).format('YYYY.MM.DD'));
        $('input[name=inputProductMonth][data-option-code=PICK_ME_UP]').val(maxCount);
    }
}

function openPickMeUp() {
    alert('결제관리로 이동합니다. 이곳에서 해당 포트폴리오를 찾으신 후 옵션을 추가하세요. ');
    location.href = '/freelancer/payment/index';
}

$(document).on('click', '#toFreelancerProfileBtn', function() {
    location.href = '/freelancer/profile/index';
});

$(document).on('click', '.contact-available-time-pick-row-delete-btn', function() {
   $(this).closest('.contact-available-time-pick-row-container').remove(); 
});

$(document).on('click', '.contact-available-time-pick-row-add-btn', function() {
    var timePickRowIndex = $('.contact-available-time-pick-row-container').length;
    if (timePickRowIndex >= 3) {
        alert('최대 3개까지 등록 가능합니다.');
        return;
    }
    $('.contact-available-time-pick-container').append('<div class="row mt-2 contact-available-time-pick-row-container">\n' +
        '<div class="col-md-6">\n' +
        '<div class="d-flex" style="align-items: center;height: 38px">\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[0]" id="mon_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="mon_' + timePickRowIndex + '" style="line-height: 24px;">월</label>\n' +
        '</div>\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[1]" id="tue_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="tue_' + timePickRowIndex + '" style="line-height: 24px;">화</label>\n' +
        '</div>\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[2]" id="wed_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="wed_' + timePickRowIndex + '" style="line-height: 24px;">수</label>\n' +
        '</div>\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[3]" id="thur_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="thur_' + timePickRowIndex + '" style="line-height: 24px;">목</label>\n' +
        '</div>\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[4]" id="fri_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="fri_' + timePickRowIndex + '" style="line-height: 24px;">금</label>\n' +
        '</div>\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[5]" id="sat_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="sat_' + timePickRowIndex + '" style="line-height: 24px;">토</label>\n' +
        '</div>\n' +
        '<div class="custom-control custom-checkbox ml-2">\n' +
        '<input type="checkbox" class="custom-control-input contactAvailableDay[6]" id="sun_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="sun_' + timePickRowIndex + '" style="line-height: 24px;">일</label>\n' +
        '</div>\n' +
        '</div>\n' +
        '</div>\n' +
        '<div class="col-md-6">\n' +
        '<div class="d-flex" style="align-items: center;">\n' +
        '<div class="custom-control custom-checkbox ml-2 mr-3">\n' +
        '<input type="checkbox" class="custom-control-input" id="holiday_' + timePickRowIndex + '">\n' +
        '<label class="custom-control-label" for="holiday_' + timePickRowIndex + '" style="line-height: 24px;white-space: nowrap;">공휴일가능</label>\n' +
        '</div>\n' +
        '<input type="text" data-plugin-timepicker class="form-control timepicker contactAvailableTime[0]">\n' +
        '<div class="px-2">~</div>\n' +
        '<input type="text" data-plugin-timepicker class="form-control timepicker contactAvailableTime[1]">\n' +
        '<i class="fas fa-minus fa-lg pl-3 text-primary contact-available-time-pick-row-delete-btn" style="cursor: pointer;"></i>\n' +
        '</div>\n' +
        '</div>\n' +
        '</div>');

    $('.contact-available-time-pick-container .timepicker').timepicker(
        {
            "showMeridian": false,
            icons: {
                up: 'fas fa-chevron-up',
                down: 'fas fa-chevron-down'
            }
        });
});

function setContactAvailableDayTime() {
    var contactAvailableDayTimeDic = {};
    $('.contact-available-time-pick-row-container').each(function() {
        var days = [];
        for (var i = 0; i <= 7; i++) {
            var checked = $(this).find('input.contactAvailableDay\\[' + i + '\\]').is(':checked');
            if (checked) {
                days.push(i);
            }
        }
        if (days.length === 0) return;

        var times = [];
        var startTime = $(this).find('input.contactAvailableTime\\[0\\]').val();
        var endTime = $(this).find('input.contactAvailableTime\\[1\\]').val();
        times.push(startTime);
        times.push(endTime);
        contactAvailableDayTimeDic[days] = times;
    });

    if (Object.keys(contactAvailableDayTimeDic).length === 0) {
        return false;
    }

    console.log(JSON.stringify(contactAvailableDayTimeDic));
    $('input[name=contactAvailableDayTime]').val(JSON.stringify(contactAvailableDayTimeDic));
    console.log($('input[name=contactAvailableDayTime]').val());

    return true;
}

$(document).on('change', '.contact-available-time-pick-container input[type=checkbox]', function(e) {
    if (!modifyMode && !$('input[name=productItem][data-product-code=DIRECT_DEAL]').is(':checked')) {
        alert('직거래 옵션을 구매하신 회원에게만 적용됩니다.:)');
        $(this).prop('checked', false);
    }
});
