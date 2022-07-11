var selectedProjectId = '';
var existsPayedPaymentToUsers = false;

var textarea = document.querySelector('textarea');

$(document).ready(function() {
    $('#videoModal').on('shown.bs.modal', function(event) {
        var videoUrl = $(event.relatedTarget).data('url');
        $('#videoModal video').attr('src', videoUrl);
    });
})

$(document).ready(function () {
    $('#paymentToUserForm').on('shown.bs.modal', function(event) {
        selectedProjectId = $(event.relatedTarget).data('project-id');
        existsPayedPaymentToUsers = JSON.parse($(event.relatedTarget).data('exists-payed'));
    });

    $('#rateModal').on('shown.bs.modal', function(event) {
        selectedProjectId = $(event.relatedTarget).data('project-id');
        existsPayedPaymentToUsers = JSON.parse($(event.relatedTarget).data('exists-payed'));
        $('#rateModal input[name=projectId]').val(selectedProjectId);
    });

    // 프리랜서의 용역대금 팝업 호출
    $('.requestPaymentToUserModalBtn').click(function (event) {
        if (!requestPaymentAvailable) {
            alert('용역 대금 청구를 위해서는 세금납부 방법, 계좌번호 등에 대한 정보가 필요합니다. 프로필 5단계에서 이에 대한 정보를 입력해주세요. 프로필 수정으로 이동합니다. ');
            location.href = '/freelancer/profile/modify?after-redirect=%2Ffreelancer%2Fproject%2FonGoingList';
            return;
        }
        var projectId = $(this).data('project-id');
        var id = $(this).data('id');
        var amount = $(this).data('amount');
        var description = $(this).data('description');

        if (id) {
            $('#paymentToUserModal input[name=id]').val(id);
            $("#paymentToUserModal input[name=amount]").val(amount);
            $("#paymentToUserModal input[name=amountWithCommas]").val(amount.toLocaleString());
            $("#paymentToUserModal textarea[name=description]").val(description);
        } else {
            $('#paymentToUserModal input[name=id]').val('');
            $("#paymentToUserModal input[name=amount]").val('');
            $("#paymentToUserModal input[name=amountWithCommas]").val('');
            $("#paymentToUserModal textarea[name=description]").val('');
        }
        $('#paymentToUserModal input[name=projectId]').val(projectId);

        if (id) {
            $('#paymentToUserModal .modal-title').text('지급청구 수정');
        } else {
            $('#paymentToUserModal .modal-title').text('지급청구');
        }

        var target = $(".requestPaymentToUserModalBtn").attr('data-target-custom');
        $(target).modal('show');
    });



    $('#paymentToUserForm').submit(function(e) {
        e.preventDefault();

        if (!confirm('클라이언트와 충분히 소통하셨나요? \n해당 내용으로 지급청구를 진행하시겠습니까?')) {
            return false;
        }

        var amount = removeComma($('#paymentToUserModal input[name=amountWithCommas]').val());
        if (!amount) {
            alert('요청금액을 입력해 주세요');
            return false;
        }

        $('#paymentToUserModal input[name=amount]').val(amount);

        var id = $('#paymentToUserModal input[name=id]').val();

        if (id) {
            $.ajax({
                method: 'PUT',
                url: '/payment-to-users/' + id + "?" + $('#paymentToUserForm').serialize(),
                success: function (data) {
                    alert('요청되었습니다.');
                    $('#paymentToUserModal').modal('hide');
                    location.reload();

                },
                error: function (error) {
                    alert('서버와의 통신 중 문제가 발생하였습니다.');
                    $('#paymentToUserModal').modal('hide');
                    console.error(error);
                }
            });
        } else {
            $.ajax({
                method: 'POST',
                url: '/payment-to-users/status/requested',
                data: $('#paymentToUserForm').serialize(),
                success: function (data) {
                    alert('요청되었습니다.');
                    $('#paymentToUserModal').modal('hide');
                    location.reload();

                },
                error: function (error) {
                    alert('서버와의 통신 중 문제가 발생하였습니다.');
                    $('#paymentToUserModal').modal('hide');
                    console.error(error);
                }
            });
        }
    });

    // 프로젝트 완료하기 팝업 호출
    $('.btnCallProjectEndPopUp').on("click", function (e) {
        var raterId = $(this).parent().find('.raterId').val();
        var ratedId = $(this).parent().find('.ratedId').val();
        var projectId = $(this).parent().find('.projectId').val();
        $('#ratePopupRaterId').val(raterId);
        $('#ratePopupRatedId').val(ratedId);
        $('#ratePopupProjectId').val(projectId);

        console.log(raterId, ", ", ratedId, ", ", projectId);
    });

    // 평가남기기(남기고 프로젝트 종료)
    $('#btnRateProjectDone').on("click", function (e) {
        var raterId = $(this).parent().find('#ratePopupRaterId').val();
        var ratedId = $(this).parent().find('#ratePopupRatedId').val();
        var projectId = $(this).parent().find('#ratePopupProjectId').val();
        var byType = "FREELANCER";
        var satisfactory = $('#satisfactory option:selected').val();
        var respectAttitude = $('#respectAttitude option:selected').val();
        var accuracy = $('#accuracy option:selected').val();
        var reliability = $('#reliability option:selected').val();
        var communication = $('#communication option:selected').val();
        var speed = null;
        var comment = $('#ratePopupComment').val();

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
        var projectId = $(this).parent().find('#ratePopupProjectId').val();
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
        var projectId = $(this).parent().find('.projectId').val();

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

$(document).on('submit','#rateForm',function(e) {
    if (!confirm('평가를 진행하시겠습니까?')) {
        e.preventDefault();
        return false;
    }
    if (!$('select[name=type1Rate] option:selected').val() || !$('select[name=type2Rate] option:selected').val()
        || !$('select[name=type3Rate] option:selected').val() || !$('select[name=type4Rate] option:selected').val()
        || !$('select[name=type5Rate] option:selected').val() || !$('textarea[name=content]').val()) {
        e.preventDefault();
        alert('입력하지 않은 항목이 있습니다.');
        return false;
    }

    e.preventDefault();

    $.ajax({
        type: 'POST',
        url: '/projects/' + selectedProjectId + '/rates',
        data: $('#rateForm').serialize(),
        success: function(data) {
            requestProjectCompleted();
        },
        error: function(error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        }
    });

    return false;
});

function requestProjectCompletedWithoutRate() {
    if (!confirm('평가 없이 완료 요청하시겠습니까?')) {
        return;
    }
    requestProjectCompleted();
}

function requestProjectCompleted() {
    if (!existsPayedPaymentToUsers) {
        alert('용역대금이 귀하에게 입금된 내역이 아직 없습니다. 입금 완료 후 프로젝트 완료처리가 가능합니다. 입금의 순서는 다음과 같습니다. 프리랜서의 지급청구 → 클라이언트의 승인 → 입금완료');
        return;
    }

    $.ajax({
        method: 'POST',
        url: '/project-completes/request?projectId=' + selectedProjectId,
        success: function(data) {
            if ('SUCCESS' === data.responseCode) {
                alert('완료처리 요청 되었습니다.');
                location.reload();
            } else {

            }
        },
        error: function(error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
            console.log(error);
        }
    })
}

$(document).on('change', 'input[name=commentFile]', function() {
    var form = $(this).closest('form');

    var formData = new FormData();
    formData.append('commentFile', $(this)[0].files[0]);
    $.ajax({
        method: 'POST',
        url: form.attr('action'),
        data: formData,
        processData: false,
        cache: false,
        contentType: false,
        success: function() {
            location.reload();
        },
        error: function() {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        },
    })

});

$(document).on('click', '.projectDetailBtn', function() {
    var projectId = $(this).data('id');
    location.href = '/freelancer/projects/' + projectId + '/details';
});

$(document).on('focus', '.comment-chat textarea[name=contents]', function(e) {
    $(this).closest('.comment-chat').find('.comment-view').show();
});

function clickAddFile(elem) {
    $(elem).siblings('input[type=file]').trigger('click');
}

function writeComment(elem) {
    if (!$(elem).parent().siblings('textarea[name=contents]').val()) {
        alert('내용을 입력해 주세요.');
        return;
    }

    if (!confirm('등록하시겠습니까?')) {
        return;
    }
    var form = $(elem).closest('form');

    $.ajax({
        method: 'POST',
        url: form.attr('action'),
        data: form.serialize(),
        success: function() {
            location.reload();
        },
        error: function() {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        },
    })
}

function toContestDetail(id) {
    location.href = '/freelancer/contest/' + id + '/details';
}

function toProjectDetail(id) {
    location.href = '/freelancer/projects/' + id + '/details';
}

function projectDetail(projectId, sufix) {
    location.href = '/freelancer/projects/' + projectId + '/details' + (sufix?sufix:'');
}

// 텍스트박스 자동늘리기

textarea.addEventListener('keydown', autosize);
 
function autosize(){
  var el = this;
  setTimeout(function(){
    el.style.cssText = 'height:auto; padding:0';
    // for box-sizing other than "content-box" use:
    // el.style.cssText = '-moz-box-sizing:content-box';
    el.style.cssText = 'height:' + el.scrollHeight + 'px';
  },0);
}