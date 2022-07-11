$(document).ready(function() {
    $('#register').click(function() {
        if (confirm('등록하시겠습니까?')) {
            var price = $('input[name=price]').val();
            var reason = $('textarea[name=reason]').val();

            if (!price || !reason) {
                alert('필수 값을 입력해 주세요');
                return;
            }

            var formData = new FormData();
            formData.append('amount', price);
            formData.append('reason', reason);

            $.ajax({
                method: 'POST',
                url: '/escrows/refund-requests',
                data: formData,
                processData: false,
                cache: false,
                contentType: false,
                success: function() {
                    alert('성공적으로 등록되었습니다.');
                    location.reload();
                },
                error: function() {
                    alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
                }
            });
        }
    })
});