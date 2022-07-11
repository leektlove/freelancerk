$(document).ready(function() {
    $(".comment-view").scrollTop($(".comment-view").scrollHeight);
    // 메시지 등록
    $('#btnReply').on("click", function(e) {
        e.preventDefault();
        if ('COMPLETED' === projectStatus) {
            return;
        }
        var projectId = $('#projectId').val();
        var content = $('#inputReply').val();
        if (!content) {
            alert(' 메시지를 입력해주세요.');
            return;
        }
        if (content.length > 1000) {
            alert('메시지은 1,000 자 이상 입력할 수 없습니다.');
        }
        var targetUserId = $('input[name=targetUserId]').val();
        var formData = new FormData();
        formData.append('content', content);
        if (targetUserId) {
            formData.append('targetUserId', targetUserId);
        }

        $.ajax({
            method: 'POST',
            url: '/projects/' + projectId + '/comments',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function() {
                $('#inputReply').val('');
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    $('input[name=projectContractFile]').change(function() {

        var formData = new FormData();
        formData.append('file', $(this)[0].files[0]);

        var projectId = $('input[name=id]').val();
        $.ajax('/projects/' + projectId + '/contract-files', {
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.responseCode === 'SUCCESS') {
                    var item = res.data;
                    $('#projectContractFileContainer').append('<div><a href="' + item['fileUrl'] + '">' + item['fileName'] + '</a> <i class="fa fa-window-close cursor projectContractFileDeleteBtn" data-id="' + item['id'] + '" aria-hidden="true"></i></div>');
                }
            },

            error: function () {
                alert('파일 저장에 실패했습니다.');
            },

            complete: function () {
            },
        });
    });

    $(document).on('click', '.projectContractFileDeleteBtn', function() {
        if (!confirm('정말 삭제하시겠습니까?')) {
            return;
        }
        var id = $(this).data('id');
        var $elem = $(this).closest('div');
        $.ajax({
            url: '/project-contract-files/' + id,
            type: 'DELETE',
            success: function() {
                $($elem).remove();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        })
    });
});

function cancelProject(projectId, numberOfApplicant) {
    var message = '';
    if (numberOfApplicant > 0) {
        message = '[주의] 현재 입찰가격을 제시한 프리랜서가 있습니다. 입찰중인 프로젝트를 중도 취소하시면 더 이상의 입찰자 열람은 불가능하며, 클라이언트의 평판에 부정적 영향을 미칠 수 있습니다. 이 프로젝트를 취소하시겠습니까?';
    } else {
        message = '취소하시면 귀하의 프로젝트는 더 이상 노출되지 않으며 견적(지원자) 내역은 삭제됩니다. 구매하신 포스팅 옵션 또한 남은 기간에 관계없이 종료되어 환불되지 않습니다. 정말 프로젝트를 취소하시겠습니까?';
    }
    if (!confirm(message)) {
        return;
    }
    $.ajax({
        type: 'PUT',
        url: '/projects/' + projectId + '?status=CANCELLED',
        success: function(data) {
            alert('프로젝트가 취소되었습니다.');
            history.back();
        },
        error: function(error) {
            alert('서버와의 통신 중 문제가 발생했습니다. 문제가 계속될 경우 고객센터로 문의주세요');
            return;
        }
    });
}

/* 채팅창 스크롤 맨아래로 내림 */
// $(document).ready(function(){
//     $(".comment-view").scrollTop($(".comment-view").scrollHeight);
// });

$(document).on('click', '.comments .receive .appointed_freelancer', function() {
    var userId = $(this).data('user-id');
    var userName = $(this).data('user-name');

    $('#freelancerUserArea').show();
    $('#freelancerUsername').text(userName);
    $('input[name=targetUserId]').val(userId);
    $('textarea[name=contents]').focus();
});

$(document).on('click', '#toFreelancerMessageBtn', function() {
    $('input[name=targetUserId]').val('');
    $('#freelancerUserArea').hide();

});