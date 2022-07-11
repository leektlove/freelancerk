function loginWithFb() {
    FB.login(function(response) {
        if (response.authResponse) {
            var accessToken = response.authResponse.accessToken;
            FB.api('/me', function(response) {
                console.log(response);
                var userId = response.id;

                var formData = new FormData();
                formData.append('username', userId);
                formData.append('role', 'ROLE_CLIENT');
                formData.append('authType', 'FACEBOOK');
                $.ajax({
                    type: "POST",
                    url: $('#form').attr('action'),
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function(data)
                    {
                        location.href = '/client/profile/index';

                    },
                    error: function(jqXHR, textStatus) {
                        alert('가입정보가 없습니다.');
                    }
                });

            });
        } else {
            console.log('User cancelled login or did not fully authorize.');
        }
    });
}

function loginWithKakao() {
    Kakao.Auth.login({
        success: function(authObj) {
            Kakao.API.request({
                url: '/v2/user/me',
                success: function(res) {
                    console.log(JSON.stringify(res));
                    console.log(JSON.stringify(authObj));
                    var userId = res.id;
                    var formData = new FormData();
                    formData.append('username', userId);
                    formData.append('role', 'ROLE_CLIENT');
                    formData.append('authType', 'KAKAO');
                    $.ajax({
                        type: "POST",
                        url: $('#form').attr('action'),
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: function (data) {
                            location.href = '/client/profile/index';

                        },
                        error: function (jqXHR, textStatus) {
                            alert('가입정보가 없습니다.');
                        }
                    });

                    console.log(res.id);
                    console.log(res.kaccount_email);
                    console.log(res.properties['nickname']);
                    console.log(authObj.access_token);
                }
            })
        },
        fail: function(err) {
            console.log(JSON.stringify(err));
        }
    });
};

function loginWithNaver() {
    var authorizeUrl = naverLogin.generateAuthorizeUrl();
    var popup = window.open(authorizeUrl, "", "width=400,height=500");
    popup.onbeforeunload = function(){
        location.reload();
        return null;
    }
}

function reload() {
    location.href = '/client/profile/index';
}