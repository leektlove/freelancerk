function invalidatePickMeUpTickets(pickMeUpId) {
    if (!confirm('해당 내역을 삭제하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'DELETE',
        url: '/pick-me-ups/' + pickMeUpId + '/purchase-records',
        success: function(response) {
            if ('SUCCESS' === response.responseCode) {
                alert('성공적으로 요청되었습니다.');
                location.reload();
            } else {
                alert('요청 중 문제가 발생했습니다.');
            }
        },
        error: function(error) {
            console.error(error);
            alert('요청 중 문제가 발생했습니다.');
        }

    })
}

function toAddContestOptionView(contestEntryId, numberOfActiveTickets) {
    if (numberOfActiveTickets >= 3) {
        alert('추가할 옵션이 없습니다.');
        return;
    }
    location.href='/freelancer/contestDetail/selectOption?contestId=' + contestEntryId + '&mode=EXTEND';
}

function invalidateContestEntryTicket(contestEntryId) {
    if (!confirm('해당 내역을 삭제하시겠습니까?')) {
        return;
    }
    $.ajax({
        type: 'DELETE',
        url: '/contest-entries/' + contestEntryId + '/purchase-records',
        success: function(response) {
            if ('SUCCESS' === response.responseCode) {
                alert('성공적으로 요청되었습니다.');
                location.reload();
            } else {
                alert('요청 중 문제가 발생했습니다.');
            }
        },
        error: function(error) {
            console.error(error);
            alert('요청 중 문제가 발생했습니다.');
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