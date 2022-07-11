$(document).ready(function () {

    $('#btnAskBilling').on("click", function(e) {
        let projectId = $('#billingProjectId').val();
        let cost = $('#blillingPrice').val();
        let reason = $('#billingReason').val();
        console.log(projectId, ", ", cost, ", ", reason);

        var formData = new FormData();
        formData.append('projectId', projectId);
        formData.append('cost', cost);
        formData.append('reason', reason);
       
        $.ajax({
            method: 'POST',
            url: '/askBilling',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert(result['message']);
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });

    });



    // 프로젝트 완료하기 팝업 호출
    $('.btnCallProjectEndPopUp').on("click", function (e) {
        let raterId = $(this).parent().find('.raterId').val();
        let ratedId = $(this).parent().find('.ratedId').val();
        let projectId = $(this).parent().find('.projectId').val();
        $('#ratePopupRaterId').val(raterId);
        $('#ratePopupRatedId').val(ratedId);
        $('#ratePopupProjectId').val(projectId);

        console.log(raterId, ", ", ratedId, ", ", projectId);
    });

    // 평가남기기(남기고 프로젝트 종료)
    $('#btnRateProjectDone').on("click", function (e) {
        let raterId = $(this).parent().find('#ratePopupRaterId').val();
        let ratedId = $(this).parent().find('#ratePopupRatedId').val();
        let projectId = $(this).parent().find('#ratePopupProjectId').val();
        let byType = "FREELANCER";
        let satisfactory = $('#satisfactory option:selected').val();
        let respectAttitude = $('#respectAttitude option:selected').val();
        let accuracy = $('#accuracy option:selected').val();
        let reliability = $('#reliability option:selected').val();
        let communication = $('#communication option:selected').val();
        let speed = null;
        let comment = $('#ratePopupComment').val();

        console.log(raterId, ", ", ratedId, ", ", projectId, ", ", byType,
        satisfactory, ", ", respectAttitude, ", ", accuracy, ", ", reliability,
        ", ", communication, ", ", speed, ", ", comment);

        var formData = new FormData();
        formData.append('raterId', raterId);
        formData.append('ratedId', ratedId);
        formData.append('projectId', projectId);
        formData.append('byType', byType);
        formData.append('satisfactory', satisfactory);
        formData.append('respectAttitude', respectAttitude);
        formData.append('accuracy', accuracy);
        formData.append('reliability', reliability);
        formData.append('communication', communication);
        formData.append('speed', speed);
        formData.append('comment', comment);
       
        $.ajax({
            method: 'POST',
            url: '/rateAndProjectComplete',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert(result['message']);
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    // 평가하지 않고 완료처리
    $('#btnNoRateProjectDone').on("click", function (e) {
        let projectId = $(this).parent().find('#ratePopupProjectId').val();
        var formData = new FormData();
        formData.append('projectId', projectId);
       
        $.ajax({
            method: 'POST',
            url: '/projectComplete',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert(result['message']);
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });



    // 찜한 프로젝트 삭제하기
    $('.btnDeleteLike').on("click", function (e) {
        e.preventDefault();
        let projectId = $(this).parent().find('.projectId').val();

        var formData = new FormData();
        formData.append('projectId', projectId);

        $.ajax({
            method: 'POST',
            url: '/deleteFromWishList',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert(result['message']);
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });
});

function registerPickMeUpFromCompleted(projectId) {
    if (!confirm('등록하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/pick-me-ups/from-projects/' + projectId,
        success: function(response) {
            if ('SUCCESS' === response.responseCode) {
                alert('포트폴리오가 등록되었습니다. 해당 포트폴리오의 [수정하기] 버튼을 클릭하셔서 설명, 조건, 이미지 등을 수정해주세요.');
                location.href = '/freelancer/pickMeUp/list';
            } else {
                alert('요청에 실패했습니다.');
            }
        },
        error: function(error) {
            console.error(error);
            alert('요청에 실패했습니다.');
        }
    })
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
