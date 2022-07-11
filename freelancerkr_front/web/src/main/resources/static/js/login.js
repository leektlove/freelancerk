$(document).on('submit','#form',function(e) {
    if (!$('input[name=username]').val()) {
        alert('이메일을 입력해주세요');
        return false;
    }
    if (!$('input[name=password]').val()) {
        alert('패스워드를 입력해주세요');
        return false;
    }

    var role = $('input[name=role]:checked').val();

    e.preventDefault();
	
    $.ajax({
        type: 'POST',
        url: '/login',
        data: $('#form').serialize(),
        success: function(data) {
            if ('ROLE_CLIENT' === role) {
                location.href='/client/profile/index';
            } else {
                location.href='/freelancer/profile/index';
            }
        },
        error: function(error) {
            alert('이메일 혹은 비밀번호가 일치하지 않습니다.');
        }
    })

    return false;
});