$(document).ready(function() {
    $('input[name=password]').keyup(function() {
        if ($(this).val().length < 4) {
            $('#password-txt').hide();
            return;
        }
        var passwordChecker = zxcvbn($(this).val());
        console.log(passwordChecker.score);
        $('#password-txt').html(passwordStrengthTxt(passwordChecker.score));
        $('#password-txt').show();
    })

    $('input[name=password_confirm]').keyup(function() {
        if ($(this).val().length < 4) {
            $('#password-confirm-txt').hide();
            return;
        }
        var passwordChecker = zxcvbn($(this).val());
        console.log(passwordChecker.score);
        $('#password-confirm-txt').html(passwordStrengthTxt(passwordChecker.score));
        $('#password-confirm-txt').show();
    })
});

function join() {
    if (!$('input[name=email]').val()) {
        alert('이메일을 입력해주세요');
        return false;
    }

    var email = $('input[name=email]').val();
    if (!validateEmail(email)) {
        alert(' 유효한 이메일주소가 아닙니다. 다시 한번 확인해주세요.');
        return false;
    }

    if (!$('input[name=password]').val()) {
        alert('비밀번호를 입력해주세요');
        return false;
    }

    if ($('input[name=password]').val() !== $('input[name=password_confirm]').val()) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }

    if ($('input[name=password]').val().length < 4) {
        alert('비밀번호를 4자이상 입력해주세요');
        return false;
    }

    if (!$('input[name=name]').val()) {
        alert('이름을 입력해주세요');
        return false;
    }

    if (!$('input[name=cellphone]').val()) {
        alert('휴대전화번호를 입력해주세요');
        return false;
    }

    // if (!$('input:checkbox[name=agree1]').is(':checked') || !$('input:checkbox[name=agree2]').is(':checked') || !$('input:checkbox[name=agree3]').is(':checked')) {
    //     alert('이용약관 등에 대한 동의가 필요합니다');
    //     return false;
    // }

    if (!$('input:checkbox[name=agree]').is(':checked')) {
        alert('이용약관 등에 대한 동의가 필요합니다');
        return false;
    }

    $.ajax({
        type: 'GET',
        url: '/users/check?email=' + $('input[name=email]').val(),
        success: function() {

            try {
                window.dataLayer = window.dataLayer || [];
                window.dataLayer.push({'pageCategory': 'client'});
            } catch (e) {
                console.error(e);
            }

            $('#signUpform').submit();
        },
        error: function(error) {
            if (error.status === 409) {
                alert('이미 회원가입이 완료된 이메일 주소입니다.  비밀번호를 잊으신 경우 [로그인]에서 [비밀번호 찾기]를 이용해보세요.');
                return false;
            }
            alert('가입 중 문제가 발생하였습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    });

    return false;
}