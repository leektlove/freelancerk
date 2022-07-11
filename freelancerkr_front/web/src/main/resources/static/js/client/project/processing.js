var selectedContestEntryId = '';
var selectedProjectId = '';
var selectedPaymentToUserId = '';
var textarea = document.querySelector('textarea');

$(document).ready(function() {

    $('#paymentToUserModal').on('shown.bs.modal', function(event) {
        selectedPaymentToUserId = $(event.relatedTarget).data('id');
        selectedProjectId = $(event.relatedTarget).data('project-id');
        selectedContestEntryId = $(event.relatedTarget).data('contest-entry-id');

        var url = '';
        if (selectedPaymentToUserId) {
            url = '/payment-to-users/' + selectedPaymentToUserId
        } else {
            url = '/payment-to-users?projectId=' + selectedProjectId + '&type=PROJECT';
        }
        $.ajax({
            type: 'GET',
            url: url,
            success: function(data) {
                var paymentToUserData = data.data;
                selectedPaymentToUserId = data.data.id;
                $('#paymentToUserAmount').text((1*paymentToUserData.amount).toLocaleString() + '원');
                $('#paymentToUserDescription').text(paymentToUserData.description)
            },
            error: function(error) {
                console.error(error);
            }
        })
    });

    $('#paymentToUserForm').submit(function(e) {
        e.preventDefault();

        if (!confirm('승인 하시겠습니까?')) {
            return false;
        }

        $.ajax({
            method: 'POST',
            url:'/payment-to-users/' + selectedPaymentToUserId + '/status/accepted',
            success: function(data) {
                alert('요청되었습니다.');
                $('#paymentToUserModal').modal('hide');
                location.reload();

            },
            error: function(error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
                $('#paymentToUserModal').modal('hide');
                console.error(error);
            }
        });
    });

    $('#denyPaymentToUserBtn').click(function() {
        if (!confirm('미승인 하시겠습니까?')) {
            return false;
        }

        $.ajax({
            method: 'POST',
            url:'/payment-to-users/' + selectedPaymentToUserId + '/status/denied',
            data: $('#paymentToUserForm').serialize(),
            success: function(data) {
                alert('요청되었습니다.');
                $('#paymentToUserModal').modal('hide');
                location.reload();

            },
            error: function(error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
                $('#paymentToUserModal').modal('hide');
                console.error(error);
            }
        });
    });

    $( "#rateModal" ).on('shown.bs.modal', function(event){
        selectedProjectId = $(event.relatedTarget).data('project-id');
        if (selectedProjectId) {
            selectedContestEntryId = null;
        } else if ($(event.relatedTarget).data('contest-entry-id')){
            selectedContestEntryId = $(event.relatedTarget).data('contest-entry-id');
        }
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

    if (selectedContestEntryId) {
        $.ajax({
            type: 'POST',
            url: '/contests/entries/' + selectedContestEntryId + '/rates',
            data: $('#rateForm').serialize(),
            success: function (data) {
                giveRewards();
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
            }
        });
    } else if (selectedProjectId) {
        $.ajax({
            type: 'POST',
            url: '/projects/' + selectedProjectId + '/rates',
            data: $('#rateForm').serialize(),
            success: function (data) {
                acceptProjectCompleted();
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
            }
        });
    }

    return false;
});

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

$(document).on('focus', '.comment-chat textarea[name=contents]', function(e) {
    $(this).closest('.comment-chat').find('.comment-view').show();
});

function clickAddFile(elem) {
    $(elem).siblings('input[type=file]').trigger('click');
}


function complete() {
    if (selectedContestEntryId) {
        if (!confirm('평가 없이 상금을 지급하시겠습니까?')) {
            return;
        }
        giveRewards();
    } else if (selectedProjectId) {
        if (confirm('평가 없이 프로젝트를 완료 하시겠습니까?')) {
            return;
        }
        acceptProjectCompleted();
    }
}

function giveRewards() {
    $.ajax({
        type: 'POST',
        url: '/contests/entries/' + selectedContestEntryId + '/rewards',
        success: function(data) {
            alert(data.message);
            if (data.data === 'COMPLETED') {
                location.href = '/client/project/doneList';
                return;
            }
            location.reload();
        },
        error: function(error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        }
    });
}

function refresh(sort, elem) {
    if ($(elem).hasClass('on')) {
        if (!$(elem).hasClass('asc')) {
            $(elem).addClass('asc')
        }
    } else {
        $(elem).siblings().removeClass('on');
        $(elem).addClass('on');
    }

    var imagePath = '';
    location.href = '/client/project/processingList?sort=' + sort;
}

function cancelItem(id) {
    if (!confirm('취소하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'PUT',
        url: '/projects/' + id + '?status=CANCELLED',
        success: function(data, textStatus, jqXHR) {
            location.reload();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert('요청 중 문제가 발생했습니다.' + jqXHR.data);
        }
    })

}

function modifyItem(id) {
    location.href='/client/posting/project?id='+id;
}

function extendItem(id) {

}

function showRatePopup(bidId) {
    if (!confirm('프리랜서에게 상금을 지급하시고 본 작품에 대한 저작권을 클라이언트로 이전하시겠습니까?')) {
        return;
    }
    selectedProjectId = '';
    selectedContestEntryId = bidId;
    $('#ratePopup input[name=contestEntryId]').val(bidId);
    $('#rateModal').modal('show');
}

function projectDetail(projectId, sufix) {
    location.href = '/client/project/' + projectId + '/details' + (sufix?sufix:'');
}

function contestDetail(contestId, sufix) {
    location.href = '/client/contest/' + contestId + '/details' + (sufix?sufix:'');
}

function acceptProjectCompleted() {
    if (!selectedProjectId) {
        giveRewards();
        return;
    }

    $.ajax({
        method: 'POST',
        url: '/project-completes/accept?projectId=' + selectedProjectId,
        success: function(data) {
            alert('완료처리 요청 되었습니다.');
            location.reload();
        },
        error: function(error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
            console.log(error);
        }
    })
}

$(document).on('click', '#zoomIn', function() {
    if (currentZoom > 150) return;
    currentZoom = currentZoom + 10;
    $('.owl-item.active').find('img').css('width', currentZoom + '%');
});

$(document).on('click', '#zoomOut', function() {
    if (currentZoom < 10) return;
    currentZoom = currentZoom - 10;
    $('.owl-item.active').find('img').css('width', currentZoom + '%');
});

$(document).on('click', '#contestEntryFileFullBtn', function(e) {
    var id = $(e.target).data('id');
    window.open("/view/contest-entries/" + id + "/details", '_blank');
});


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