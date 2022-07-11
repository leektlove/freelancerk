$(document).ready(function() {
    $('input:checkbox[name=RememberMe]').change(function () {
        if (this.checked) {
            alert('개인정보 보호를 위해 공용 PC에서는 사용에 유의해 주시기 바랍니다.');
        }
        return;
    });

    $('#form').submit(function(e) {
		console.log("로그인시도");
        e.preventDefault();

        var formData = new FormData();
        formData.append('username', $('input[name=username]').val());
        formData.append('password', $('input[name=password]').val());
        formData.append('role', 'ROLE_FREELANCER');
        formData.append('authType', 'EMAIL');
        formData.append('redirectUrl', $('input[name=redirectUrl]').val());
		
        $.ajax({
            type: "POST",
            url: $('#form').attr('action'),
            data: formData,
            processData: false,
            contentType: false,
            success: function(data)
            {
                var redirectUrl = $('input[name=redirectUrl]').val();
                if (!redirectUrl || 'null' === $('input[name=redirectUrl]').val()) {
                    location.href = '/freelancer/profile/index';
                } else {
                    location.href = redirectUrl;
                }

            },
            error: function(jqXHR, textStatus) {
                if (jqXHR.status === 404) {
                    alert("존재하지 않는 이메일주소입니다. 다시 한번 확인해주세요. 혹시 SNS계정으로 회원 가입하셨다면, SNS로그인을 시도해주세요.");
                } else if (jqXHR.status === 401) {
                    alert('비밀번호가 틀렸습니다. 확인 후 다시 입력해주십시오.');
                } else if (jqXHR.status === 429) {
                    console.log(jqXHR);
                    alert('오류횟수를 초과하였습니다. 5회 이상 오류 시, 24시간 동안 이용이 불가하며, 비밀번호 찾기(등록된 이메일로 전송)를 통해 해결할 수 있습니다.');
                } else {

                }
            }
        });
        return false;
    })
});