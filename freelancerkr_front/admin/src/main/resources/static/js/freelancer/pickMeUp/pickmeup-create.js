$(window).bind("pageshow", function() {
    // update hidden input field
    $('input[name=title]').val('');
    $('textarea[name=description]').val('');
    $('input[name=portfolioMainPic]').val('');

});

$(document).ready(function () {

    $('#startAt').datepicker({autoclose: true,
        language: "ko",
    }).datepicker('setDate', moment().subtract(1, 'd').format('YYYY-MM-DD'));
    $('#endAt').datepicker({autoclose: true,
        language: "ko",
        endDate: 'today',
    }).datepicker('setDate', moment().format('YYYY-MM-DD'));

    $.ajax({
        method: 'GET',
        url: '/categories',
        cache: false,
        contentType: false,
        processData: false,
        success: function (result) {
            for (var i = 0; i < result.length; i++) {
                var mainCategoryId = result[i]["id"];
                $("#selectMainCategory").append(new Option(result[i]["name"], mainCategoryId));
            }
        },
        error: function (err, textStatus, xhr) {
            // console.log(err);
        }
    });

    // 패키지 상품 CSS 처리
    var backgroundColor = ["bg-warning", "bg-success", "bg-info", "bg-primary"];
    $("[id='packageProduct']").each(function (index, element) {
        // console.log(index, ", ", element);
        // console.log(backgroundColor[index]);
        $(element).addClass(backgroundColor[index]);
    });

    $('#toSelectOptionViewBtn').click(function(e) {
        var category2 = $('input[name=category2nd]:checked').val();
        var title = $('input[name=title]').val();
        var description = $('textarea[name=description]').val();
        var mainImageUrl = $('input[name=portfolioMainPic]').val();
        var payTypeAgreement = $("input[name=payTypeAgreement]:checked").val();
        var markupContent = $('#summernote').summernote('code');
        var workPlace = $('select[name=workPlace] option:selected').val();
        var payType = $('select[name=payType] option:selected').val();

        $("input[name=payTypeNeedAgreement]").val(JSON.parse(payTypeAgreement));

        if (!category2) {
            e.preventDefault();
            alert('프로필에서 정하신 섹터 중 하나를 선택해주세요.');
            return false;
        }

        if (!title) {
            e.preventDefault();
            alert('제목을 입력해 주세요');
            return false;
        }

        if (contentType !== 'BLOG' && !description) {
            e.preventDefault();
            alert('포트폴리오에 대한 소개를 입력해주세요.');
            return false;
        }

        if (contentType === 'BLOG' && !$(markupContent).text()) {
            e.preventDefault();
            alert('등록하시려는 블로그 형식의 컨텐츠가 입력되지 않았습니다.');
            return false;
        }

        if (contentType === 'IMAGE' && !mainImageUrl) {
            e.preventDefault();
            alert('대표 이미지를 선택 후 업로드 해주세요.');
            return false;
        }

        if (!JSON.parse($("input[name=payTypeAgreement]:checked").val()) && !$('input[name=minimumPay]').val()) {
            e.preventDefault();
            alert('최소 희망 보수를 선택(입력)해주세요.');
            return;
        }

        if (!$('input[name=minimumPay]').val()) {
            $('input[name=minimumPay]').val(0);
        }

        if (!workPlace) {
            e.preventDefault();
            alert('선호하시는 작업위치를 선택해주세요.');
            return;
        }

        if (workPlace === 'OFF_LINE' && (!$('input[name=workPlaceAddress1]').val() || !$('input[name=workPlaceAddress2]').val())) {
            e.preventDefault();
            alert('선호하시는 작업위치를 선택해주세요.');
            return;
        }

        if ($('.template-upload').length > 0) {
            e.preventDefault();
            alert('업로드 완료 하지 않은 추가 이미지 혹은 동영상 내역이 있습니다. 업로드 완료 혹은 취소 해주세요.');
            return false;
        }

        $('input[name=mainImageUrl]').val(mainImageUrl);
        $('input[name=subImageUrl]').remove();

        var elems = $('#fileupload input[name=subPiecesFileUrl\\[\\]]');

        for (var i = 0; i<elems.length; i++) {
            var elem = elems[i];
            $('#form').append(
                '<input type="hidden" name="subImageUrl[]" value="' + $(elem).val() + '" />'
            );
        }

        var contactAvailableDayTimeExits = setContactAvailableDayTime();
        if ($('input[name=productItem][data-product-code=DIRECT_DEAL]').is(':checked') && !contactAvailableDayTimeExits) {
            alert('연락 가능한 요일과 시간을 입력해 주세요.');
            e.preventDefault();
            return false;
        }
        $('#form').submit();
    });

    $('#toNextStepBtn').click(function (e) {
        var selectedProductList = [];

        var pickMeUpChecked = $('input[name=productItem][data-product-code=PICK_ME_UP]').is(':checked');
        if (!pickMeUpChecked && parseInt(removeComma($('#sumOfProductMoney').val())) === 0) {

            if (!confirm('결제하실 금액이 없습니다. 픽미업 공개 없이 나의 포트폴리오에만 저장하시겠습니까?')) {
                e.preventDefault();
                return false;
            }
        }

        $(".checkboxProductItem").each(function (index, element) {
            var topParentElement = $(element).closest('tr');

            var isDisabled = $(element).attr("disabled");
            var isChecked = $(element).is(":checked");

            if (index === 0 && isDisabled === "disabled") {
                return;
            }

            if (isDisabled === undefined && isChecked === true) {
                var productId = topParentElement.find('.productId').val();
                var month = topParentElement.find('.inputProductMonth').val();
                var money = 0;

                if (topParentElement.attr('class') === 'standardProduct') {
                    // if ( month <= remainFreeChargeProductCount) {
                    //     month = 0;
                    // } else {
                    //     month = month - remainFreeChargeProductCount;
                    // }
                    money = topParentElement.find('.basicProductMoney').val();
                } else if (topParentElement.attr('class') === 'basicProduct') {
                    money = topParentElement.find('.basicProductMoney').val();
                } else {
                    money = topParentElement.find('.packageProductMoney').val();
                }

                var excludeCalculation = $(element).data('exclude-at-calculation');

                if (!month) {
                    month = 0;
                }
                // console.log("id : ", id, ", name : ", name, ", month : ", month, ", money : ", money);
                selectedProductList.push({
                    "freelancerProductItemTypeId": productId,
                    "count": month,
                    "amount": money,
                    "type": 'FOR_PICK_ME_UP',
                    "includedInPackage": excludeCalculation
                });
            }
        });

        var jsonSelectedProductList = JSON.stringify(selectedProductList);
        if (selectedProductList.length === 0) {
            sleep(500);
            if (!confirm('프로젝트 입찰에 지원하시는 경우, 해당 프로젝트의 클라이언트만 열람할 수 있습니다. 전체공개를 원하시면 [픽미업공개] 옵션을 이용해주세요. 내 포트폴리오에만 등록하시겠습니까? ')) {
                e.preventDefault();
                return false;
            }
        }
        $('#inputSelectedProductList').val(jsonSelectedProductList);
        var contactAvailableDayTimeExits = setContactAvailableDayTime();
        if ($('input[name=productItem][data-product-code=DIRECT_DEAL]').is(':checked') && !contactAvailableDayTimeExits) {
            alert('연락 가능한 요일과 시간을 입력해 주세요.');
            e.preventDefault();
            return false;
        }
        $('#form').submit();
    });

    $('#submitWithoutOption').click(function() {
        if (!confirm('등록하시겠습니까?')) {
            return false;
        }

        $('.checkboxProductItem').prop('checked', false);
        var contactAvailableDayTimeExits = setContactAvailableDayTime();
        if ($('input[name=productItem][data-product-code=DIRECT_DEAL]').is(':checked') && !contactAvailableDayTimeExits) {
            alert('연락 가능한 요일과 시간을 입력해 주세요.');
            e.preventDefault();
            return false;
        }
        $('#form').submit();
    })
});