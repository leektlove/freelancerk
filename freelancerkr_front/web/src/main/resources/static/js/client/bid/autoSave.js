$(document).ready(function() {

});

function startProgress(id) {
    if (!confirm('입찰을 시작하시겠습니까?')) {
        return;
    }
    $.ajax({
        type: 'PUT',
        url: '/projects/' + id + '?status=POSTED',
        success: function(data, textStatus, jqXHR) {
            location.reload();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert('요청 중 문제가 발생했습니다.' + jqXHR.data);
        }
    })
}



function modifyProjectItem(id, type) {
    location.href = '/client/posting/project?id=' + id;
}

function modifyContestItem(id, type) {
    location.href = '/client/posting/contest?id=' + id;
}

function toContestPosting(id) {
    location.href = '/client/posting/contest?id=' + id
}

function deleteProject(id) {
    if (!confirm('정말 삭제하시겠습니끼?')) {
        return;
    }
    $.ajax({
        url: '/projects/' + id,
        type: 'DELETE',
        success: function() {
            location.reload();
        },
        error: function() {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요');
        }
    });
}

