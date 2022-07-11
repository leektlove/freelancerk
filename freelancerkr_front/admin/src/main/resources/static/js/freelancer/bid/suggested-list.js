$(document).ready(function () {
    console.log("suggested-list.js");

    // 입찰팝업 호출 버튼
    $('.applyModal').on("click", function (e) {
        e.preventDefault();
        let userId = $(this).parent().find('.clientId').val();
        let projectId = $(this).parent().find('.projectId').val();
        let projectPropositionId = $(this).parent().find('.projectPropositionId').val();
        let projectTitle = $(this).closest('.project').find('.project-item-subject').text();
        console.log('userID : ', userId, "projectId : ", projectId);
        $('#popUpProjectId').val(projectId);
        $('#popUpProjectPropositionId').val(projectPropositionId);

        $.ajax({
            method: 'GET',
            url: '/findUser?userId=' + userId,
            cache: false,
            contentType: false,
            processData: false,
            success: function (result) {
                $('.popUpProjectTitle').text(projectTitle);
                $('#popUpUserName').text(result['corporateName']);
                if (result['exposeEmail']) {
                    $('#popUpUserEmail').text(result['email']);
                    $('#popUpUserEmail').show();
                } else {
                    $('#popUpUserEmail').text('');
                    $('#popUpUserEmail').hide();
                }
                if (result['exposeCellphone']) {
                    $('#popUpMobile').text(result['cellphone']);
                    $('#popUpMobile').show();
                } else {
                    $('#popUpMobile').text('');
                    $('#popUpMobile').hide();
                }

                if (result['exposeSns']) {
                    $('.sns-container').show();
                    $('.sns-container .kakaoId').text(result['kakaoId']);
                    $('.sns-container .facebookId').text(result['facebookId']);
                    $('.sns-container .nateOnId').text(result['nateOnId']);
                    $('.sns-container .naverId').text(result['naverId']);
                    $('.sns-container .telegramId').text(result['telegramId']);
                } else {
                    $('.sns-container').hide();
                    $('.sns-container .kakaoId').text('');
                    $('.sns-container .facebookId').text('');
                    $('.sns-container .nateOnId').text('');
                    $('.sns-container .naverId').text('');
                    $('.sns-container .telegramId').text('');
                }
            },
            error: function (err, textStatus, xhr) {
                // console.log(err);
            }
        });
    });

    // 입찰 신청
    $('.btnApplyBid').on("click", function (e) {
        e.preventDefault();
        let projectId = $('#popUpProjectId').val();
        let projectPropositionId = $('#popUpProjectPropositionId').val();
        let amount = $('#bidAmount').val();

        var formData = new FormData();
        formData.append('projectId', projectId);
        formData.append('projectPropositionId', projectPropositionId);
        formData.append('amount', removeComma(amount));
        formData.append('bidType', 'PROJECT_BID');
        formData.append('bidStatus', 'APPLY');

        $.ajax({
            method: 'POST',
            url: '/applyProposedBid',
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

    // 거절팝업 호출 버튼
    $('.rejectModal').on("click", function(e) {
        e.preventDefault();
        let projectId = $(this).parent().find('.projectId').val();
        $('#popUpBidId').val(projectId);
    });

    // 제안 거절 버튼
    $('.btnDenyBid').on("click", function (e) {
        e.preventDefault();

        let selVal = $('#popUpSelectReject option:selected').val();

        let propId = $(this).parent().find('#popUpBidId').val();
        let rejectReason =  $('#popUpSelectReject option:selected').text();
        if(selVal == 2) {
            rejectReason = $('#popUpTxtReject').val();
        }       

        var formData = new FormData();
        formData.append('propId', propId);
        formData.append('rejectReason', rejectReason);

        $.ajax({
            method: 'POST',
            url: '/rejectProposedBid',
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

$(document).on('change', 'select[name=sortBy]', function() {
    location.href = window.location.href.split('?')[0] + '?sortBy=' + $(this).val();
});

function viewContestDetail(id) {
    if (!userInfoInput) {
        if (confirm('프로필 업로드 완료 후 가능합니다.')) {
            location.href='/freelancer/profile/modify';
            return;
        }
        return;
    }
    location.href='/freelancer/contests/' + id + '/details';
}