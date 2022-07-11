$(document).ready(function () {
    // 입찰 취소
    $('.btnBidCancel').on("click", function (e) {
        if (!confirm('정말 취소하시겠습니까?')) {
            return;
        }
        let bidId = $(this).data('id');

        var formData = new FormData();
        formData.append('bidId', bidId);

        $.ajax({
            method: 'POST',
            url: '/deleteBid',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function (result) {
                alert(result['message']);
                location.reload();
            },
            error: function () {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    // 금액 수정하기 버튼
    $('.btnChangeBidPrice').on("click", function (e) {
        let clientName = $(this).parent().parent().find('.txtClientName').val();
        let projectBidId = $(this).parent().parent().find('.txtBidId').val();
        console.log("cn : ", clientName, ", bid : ", projectBidId);

        $('#popUpClientName').text(clientName);
        $('#popUpBidId').val(projectBidId);
        $('#popupClientEmail').text($($(this).parent().parent().find('input[name=clientEmail]')[0]).val());
        $('#popupClientCellphone').text($($(this).parent().parent().find('input[name=clientCellphone]')[0]).val());
    });

    // 팝업 금액 수정
    $('#popUpSubmit').on("click", function (e) {
        let bidId = $('#popUpBidId').val();
        let price = 1*removeComma($('#popUpBidPrice').val());
        console.log("bidId : ", bidId, "price : ", price);


        var formData = new FormData();
        formData.append('bidId', bidId);
        formData.append('price', price);

        $.ajax({
            method: 'POST',
            url: '/bidPrice',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function (result) {
                alert(result['message']);
                location.reload();
            },
            error: function () {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    $('#edit_price').on('show.bs.modal', function (e) {
        // do something...
        console.log($(this).data('id'));
    })
});

$(document).on('change', 'select[name=sortBy]', function() {
    location.href = window.location.href.split('?')[0] + '?sortBy=' + $(this).val();
});

function projectDetail(id, hashbang) {
    var url = '/freelancer/projects/' + id + '/details';
    if (url) {
        url += hashbang;
    }
    location.href = url;
}

function contestDetail(id, hashbang) {
    var url = '/freelancer/contests/' + id + '/details';
    if (url) {
        url += hashbang;
    }
    location.href = url;
}