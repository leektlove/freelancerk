$(document).ready(function() {
    $('input#price').keyup(function() {
        $('#escrow_needpay').html(Math.floor(1.1*removeComma($(this).val())).toLocaleString());
    });

    $('#start_pjt').submit(function(e) {
        if (!confirm('프로젝트를 진행하시겠습니까?')) {
            return false;
        }

        var name = $('input[name=name]').val();
        var description = $('textarea[name=description]').val();
        var price = $('input[name=price]').val();

        if (!name) {
            alert('프로젝트 이름을 입력해주세요');
            $('input[name=name]').focus();
            return false;
        }

        if (!description) {
            alert('프로젝트 상세내용을 기재해주세요.');
            $('textarea[name=description]').focus();
            return false;
        }

        if (!price) {
            alert('계약예정금액을 입력해주세요.');
            $('input[name=price]').focus();
            return false;
        }

        $('input[name=price]').val(removeComma(price));

        e.preventDefault();

        $.ajax({
            type: "POST",
            url: $('#start_pjt').attr('action'),
            data: new FormData($('#start_pjt')[0]),
            processData: false,
            contentType: false,
            success: function(data)
            {
                location.href = '/client/project/processingList';

            },
            error: function() {
                alert('요청에 실패했습니다. 다시 시도해주시거나, 문제가 계속될 경우 고객센터로 문의 주세요');
            }
        });
        return false;
    });

    $('#cancelUploadFileBtn').click(function() {
        $('#preview_filename').empty();
        $('input[name=projectDescriptionFile]').val('');
    });
});

function countChar(elem) {
    var content = $(elem).val();
    var len = content.length;
    if (len >= 1000) {
        $(elem).val(content.substring(0, 1000));
    } else {
        $('#textCnt').text(len.toLocaleString());
    }
}