window.fbAsyncInit = function() {
    FB.init({
        appId      : facebookAppId,
        cookie     : true,
        xfbml      : true,
        version    : 'v5.0'
    });

    FB.AppEvents.logPageView();

};

(function(d, s, id){
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {return;}
    js = d.createElement(s); js.id = id;
    js.src = "https://connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

Kakao.init(kakaoAppId);

var naverLogin = new naver.LoginWithNaverId(
    {
        clientId: naverClientId,
        callbackUrl: naverCallbackUrl,
        isPopup: false, /* 팝업을 통한 연동처리 여부 */
        loginButton: {color: "green", type: 3, height: 60}
    }
);

/* 설정정보를 초기화하고 연동을 준비 */
naverLogin.init();