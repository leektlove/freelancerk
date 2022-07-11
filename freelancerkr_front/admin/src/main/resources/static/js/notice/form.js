$(document).ready(function() {
    $('#summernote').summernote({
        lang: 'ko-KR',
        height: 500,
    });

    $('#summernote').summernote('code', contents);

    $('#form').submit(function (e) {
        e.preventDefault();

        if (!confirm('계속 진행하시겠습니끼?')) {
            return false;
        }

        var title = $('input[name=title]').val();
        var markupContent = $('#summernote').summernote('code');

        if (!title || !markupContent) {
            alert('필수 값을 입력해 주세요.');
            return false;
        }

        $.ajax({
            type: 'POST',
            url: '/notices',
            data: $('#form').serialize(),
            success: function(response) {
                if ('SUCCESS' === response.responseCode) {
                    location.href = '/notice/'
                } else {
                    alert('요청 중 문제가 발생했습니다.');
                }
            },
            error: function(error) {
                alert('요청 중 문제가 발생했습니다.');
                console.error(error);
            }
        })
        return true;
    })
});