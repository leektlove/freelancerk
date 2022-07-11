function cancelItem(id, numberOfApplicant, projectType) {
    var message = '';
    var successMessage = '';
    if ('PROJECT' === projectType) {
        if (numberOfApplicant > 0) {
            message = '[주의] 현재 입찰가격을 제시한 프리랜서가 있습니다. 입찰중인 프로젝트를 중도 취소하시면 더 이상의 입찰자 열람은 불가능하며, 클라이언트의 평판에 부정적 영향을 미칠 수 있습니다. 이 프로젝트를 취소하시겠습니까?';
        } else {
            message = '취소하시면 귀하의 프로젝트는 더 이상 노출되지 않으며 견적(지원자) 내역은 삭제됩니다. 구매하신 포스팅 옵션 또한 남은 기간에 관계없이 종료되어 환불되지 않습니다. 정말 프로젝트를 취소하시겠습니까?';
        }
    } else if ('CONTEST' === projectType) {
        if (numberOfApplicant > 0) {
            message = '본 컨테스트에는 제출된 작품이 있습니다. 컨테스트를 취소하시면 귀하에게 저작권이 이전되지 않으며, 보증금과 상금의 일부만 환불받게 됩니다. 정말 취소하시겠습니까?';
            successMessage = '관리자 확인 후 취소일 7일 내 환불(결제취소)이 진행될 예정입니다. 이용해주셔서 감사합니다.';
        } else {
            message = '취소하시면 상금과 보증금이 7일 내 환불(카드취소)됩니다. 정말 해당 컨테스트를 취소하시겠습니까?';
            successMessage = '관리자 확인 후 취소일 7일 내 환불(결제취소)이 진행될 예정입니다. 이용해주셔서 감사합니다.';
        }
    }
    if (!confirm(message)) {
        return;
    }

    $.ajax({
        type: 'PUT',
        url: '/projects/' + id + '?status=CANCELLED',
        success: function(data, textStatus, jqXHR) {
            if (successMessage) {
                alert(successMessage);
            }
            location.href='/client/bid/processingList';
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert('요청 중 문제가 발생했습니다.' + jqXHR.data);
        }
    })

}

function extendItem(id) {
    if (!confirm('연장하시겠습니까?')) {
        return;
    }

    var projectName = '';

    IMP.request_pay({
        pg : 'inicis',
        pay_method : 'card',
        merchant_uid : 'merchant_' + new Date().getTime(),
        name : '[프리랜서코리아] 프로젝트(' + projectName + ') 포스팅 기한 연장',
        amount :  50000,
        buyer_email : userEmail,
        buyer_name : userName,
        buyer_tel : userCellphone,
        m_redirect_url : '',
    }, function(rsp) {
        alert('정상 처리 되었습니다.');
    });
}

function deleteItem(id) {
    if (!confirm('삭제하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'DELETE',
        url: '/projects/' + id,
        success: function(data, textStatus, jqXHR) {
            location.reload();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert('요청 중 문제가 발생했습니다.' + jqXHR.data);
        }
    })
}