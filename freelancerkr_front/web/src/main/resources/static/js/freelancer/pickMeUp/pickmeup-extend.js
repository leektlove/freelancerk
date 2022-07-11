$(document).ready(function () {

    if ($('input[data-product-code=PICK_ME_UP]').is(':checked')) {
        $('input[type=checkbox].checkboxProductItem').prop('disabled', false);
    }

    $('#submit').click(function () {
        var selectedProductList = [];

        $(".checkboxProductItem").each(function (index, element) {
            var topParentElement = $(element).closest('tr');

            var isDisabled = $(element).attr("disabled");
            var isChecked = $(element).is(":checked");

            if (index === 0 && isDisabled === "disabled") {
                return;
            }

            if (isDisabled === undefined && isChecked === true) {
                var productId = topParentElement.find('.productId').val();
                var month = topParentElement.find('input[name=count]').val();

                if (!month || month === 0) {
                    return;
                }
                var money = 0;

                if (topParentElement.attr('class') === 'standardProduct') {
                    money = topParentElement.find('.basicProductMoney').val();
                } else if (topParentElement.attr('class') === 'basicProduct') {
                    money = topParentElement.find('.basicProductMoney').val();
                }

                selectedProductList.push({
                    "freelancerProductItemTypeId": productId,
                    "count": month,
                    "amount": money,
                });
            }
        });

        var jsonSelectedProductList = JSON.stringify(selectedProductList);
        // if (selectedProductList.length === 0) {
        //     if (!confirm('프로젝트 입찰에 지원하시는 경우, 해당 프로젝트의 클라이언트만 열람할 수 있습니다. 전체공개를 원하시면 [픽미업공개] 옵션을 이용해주세요. 내 포트폴리오에만 등록하시겠습니까? ')) {
        //         e.preventDefault();
        //         return false;
        //     }
        // }
        $('#inputSelectedProductList').val(jsonSelectedProductList);
        var contactAvailableDayTimeExits = setContactAvailableDayTime();
        if ($('input[name=productItem][data-product-code=DIRECT_DEAL]').is(':checked') && !contactAvailableDayTimeExits) {
            alert('연락 가능한 요일과 시간을 입력해 주세요.');
            return false;
        }
        return true;
    });
});
