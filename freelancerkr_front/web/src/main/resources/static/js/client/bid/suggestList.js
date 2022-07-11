function deleteProposition(id, status) {
    if ('ACCEPT' === status || 'REJECT' === status) {
        alert('프리랜서가 이미 수락 또는 거절을 완료했습니다.');
        return;
    }

    if (!confirm('정말 취소하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'DELETE',
        url: '/project-propositions/' + id,
        success: function () {
            location.reload();
        },
        error: function() {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의주세요.');
        }
    })
}
