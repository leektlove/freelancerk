$(document).ready(function () {
    // 파일 수정
    init();

    $('.btnModifyFile').click(function (e) {
        var topParent = $(this).parent().parent().parent();

        var fileId = $(this).parent().find('.fileId').val();
        var fileTitle = topParent.find('.inputFileTitle').val();
        var representative = topParent.find('.radioBtn').is(':checked');
        var newFile = null;
        console.log(topParent.find(".inputFile"));
        if ($(this).parent().parent().find('.inputFile')[0].files != null) {
            newFile = $(this).parent().parent().find('.inputFile')[0].files[0];
        }

        console.log('fileId : ', fileId, ", fileTitle : ", fileTitle, ", representative : ", representative, "file : ", newFile);

        var formData = new FormData();
        formData.append('fileId', fileId);
        formData.append('fileTitle', fileTitle);
        formData.append('newFile', newFile);
        formData.append('representative', representative);

        $.ajax({
            method: 'POST',
            url: '/modifyContestFile',
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

    // 파일 삭제
    $('.btnDeletFile').click(function (e) {
        var topParent = $(this).parent().parent().parent();
        var fileId = $(this).parent().find('.fileId').val();
        var listSize = $(this).parent().find('.listSize').val();
        var representative = topParent.find('.radioBtn').is(':checked');

        var isCheck = true;

        if (representative) {
            alert('대표 파일을 삭제하실 수 없습니다.');
            isCheck = false;
        }

        if (isCheck && listSize < 2) {
            alert('파일이 1개인 경우에는 삭제하실 수 없습니다.');
            isCheck = false;
        }

        if (isCheck) {
            var formData = new FormData();
            formData.append('fileId', fileId);

            $.ajax({
                method: 'POST',
                url: '/deleteContestFile',
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
        }
    });


    // 파일 추가
    $('.btnAddFile').click(function (e) {
        var topParent = $(this).parent().parent().parent();
        var bidId = $('#bidId').val();
        var fileType = $('#fileType').val();
        var fileTitle = topParent.find('.inputFileTitle').val();
        var representative = topParent.find('.radioBtn').is(':checked');
        var file = null;
        if ($(this).parent().parent().find('.inputFile')[0].files != null) {
            file = $(this).parent().parent().find('.inputFile')[0].files[0];
        }

        var formData = new FormData();
        formData.append('bidId', bidId);
        formData.append('fileType', fileType);
        formData.append('fileTitle', fileTitle);
        formData.append('file', file);
        formData.append('representative', representative);

        $.ajax({
            method: 'POST',
            url: '/addContestFile',
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


    // 계약서 파일 추가
    $('.btnAddContractFile').click(function (e) {
        var projectId = $('#contestId').val();
        var whoAdd = "FREELANCER";
        var file = null;

        console.log($(this).parent().find('.inputContractFile'));
        if ($(this).parent().find('.inputContractFile')[0].files != null) {
            file = $(this).parent().find('.inputContractFile')[0].files[0];
        }

        var formData = new FormData();
        formData.append('projectId', projectId);
        formData.append('whoAdd', whoAdd);
        formData.append('file', file);

        $.ajax({
            method: 'POST',
            url: '/addContractFile',
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
    // 메시지 등록
    $('#btnReply').on("click", function(e) {
        e.preventDefault();
        if (bidCompleted) {
            alert('입찰이 종료되어 메시지 전송이 불가능합니다.');
            return false;
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
        var formData = new FormData();
        formData.append('content', content);

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

});

function init() {
    $('.slim.align-self-center').each(function () {
        var previewUrl = $(this).data('preview-url');
        $(this).find('.slim-result img.in').attr('src', previewUrl);
        $(this).find('.slim-result img.in').css('opacity', 1);
    })
}

function denyProjectProposition(projectPropositionId) {
    if (!confirm('제안을 거절하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/project-propositions/' + projectPropositionId + '/deny',
        success: function() {
            alert('성공적으로 요청되었습니다.')
        },
        error: function() {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        }

    })
}

function goToPickView(contestId, numberOfApply) {
    if (parseInt(numberOfApply) === 0) {
        alert('제출된 작품이 없습니다. 작품이 제출되면 확인 후 선정하세요. ');
        return;
    }
    location.href = '/client/contest/' + contestId + '/pick';
}

$(".comment-view").scrollTop($(".comment-view").scrollHeight);

