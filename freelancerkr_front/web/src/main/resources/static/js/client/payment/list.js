function toExtendView(id) {
    location.href = '/client/posting/projectOption?projectId=' + id + '&mode=EXTEND';
}

function toExtendContestView(id) {
    location.href = '/client/posting/contestOption?contestId=' + id + '&mode=EXTEND';
}

function deletePurchaseRecord(projectId) {
    if (!confirm('해당 포스트에 대한 옵션 결제 내역을 삭제하시겠습니까? ')) {
        return;
    }

    $.ajax({
        type: 'DELETE',
        url: '/projects/' + projectId + '/purchase-record',
        success: function(response) {
            if ('SUCCESS' === response.responseCode) {
                location.reload();
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