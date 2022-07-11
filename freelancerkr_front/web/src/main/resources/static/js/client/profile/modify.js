$(document).ready(function() {

    init();

    $("#btn-check-nickname").click(function() {
        checkNicknameDuplicate();
    });

    $('input[name=nickname]').on('keyup', function() {
        $('#btn-check-nickname').css('visibility', 'visible');
    });

    $('select[name=useEscrow]').change(function() {
        var useEscrow = $(this).val();
        if ('true' === useEscrow) {
            $('.refund-info-container').show();
        } else {
            $('.refund-info-container').hide();
        }
    });

    $('select[name=businessType]').change(function() {
        if ($(this).val() === 'INDIV_BUSINESS') {
            $('.company-info-container').show();
        } else if ($(this).val() === 'CORP_BUSINESS') {
            $('.company-info-container').show();
        } else {
            $('.company-info-container').hide();
        }
    });

    $('input[name=businessLicenseFile]').on('change', function() {
        $('#attachedFileCancelUpload').show();
        $('.real-file-input').show();
        $('.fake-file-input').hide();
    });

    $('input[name=businessLicenseFile]').on('click', function() {
        $(this).val('');
    });

    $('#attachedFileCancelUpload').click(function() {
        $('input[name=businessLicenseFile]').val('');
        $(this).hide();
        $('.real-file-input').show();
        $('.fake-file-input').hide();
    });

    $('#btn-save').on('click', function(){

        var name = $('input[name=name]').val();
        var nickname = $('input[name=nickname]').val();
        var email = $('input[name=email]').val();
        var cellphone = $('input[name=cellphone]').val();
        var businessType = $('select[name=businessType]').val();

        if (!name) {
            alert('담당자명을 입력해 주세요');
            return;
        }
        if (!nickname) {
            alert('닉네임을 입력해 주세요');
            return;
        }
        if (!nicknameAvailableMap[nickname]) {
            alert('닉네임 중복체크를 해주세요');
            return;
        }
        if (authType !== 'EMAIL' && !email) {
            alert('이메일을 입력해 주세요');
            return;
        }
        if (!cellphone) {
            alert('휴대폰번호를 입력해주세요.');
            return;
        }

        // if(!$('input:checkbox[name=exposeEmail]').is(":checked") && !$('input:checkbox[name=exposeCellphone]').is(":checked") &&
        //     !$('input:checkbox[name=exposeSns]').is(":checked")) {
        //     alert('1개 이상의 커뮤니케이션 수단이 선택되어야 합니다.');
        //     return;
        // }

        if (!businessType) {
            alert('어떤 종류의 사업자인지 선택해주세요.');
            return;
        }

        if (businessType !== 'INDIVIDUAL') {
            var corporateName = $('input[name=corporateName]').val();
            if (!corporateName) {
                alert('회사명을 입력해주세요');
                return;
            }
        }
        var password = $('input[name=password]').val();
        var passwordConfirm = $('input[name=passwordConfirm]').val();

        if (password || passwordConfirm) {
            if (password !== passwordConfirm) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }
        }

        $.ajax({
            url: '/users/client/modifications',
            method: 'POST',
            data: new FormData($('#clientProfileForm')[0]),
            processData: false,
            cache: false,
            contentType: false,
            success: function() {
                alert('프로필입력이 완료되었습니다!');
                location.href = afterRedirect;
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
        
    });

    $('.pms_c').click(function() {
        $('input[name=profileImageUrl]').val($(this).attr('src'));
        $('.slim-result img.in').attr('src', $(this).attr('src'));
        $('.slim-result img.in').css('opacity', 1);
    });

    $('.btn-cancel').click(function() {
        history.back();
    });

    $('#btn-check-nickname').css('visibility', 'hidden');
});

function init() {
    if (profileImageUrl) {
        $('.slim-result img.in').attr('src', profileImageUrl);
        $('.slim-result img.in').css('opacity', 1);
    }
    if (!businessType) {
        businessType = 'CORP_BUSINESS';
        $('option[value="CORP_BUSINESS"]').prop('selected', true);
    }

    if (businessType && (businessType === 'INDIV_BUSINESS' || businessType === 'CORP_BUSINESS')) {
        $('.company-info-container').show();
    } else {
        $('.company-info-container').hide();
    }

    if (useEscrow) {
        $('.refund-info-container').show();
    }

    if($("input[name='exposeSns']").prop("checked")){
        $(".cm_display_1_3_con").show();
    }else{
        $(".cm_display_1_3_con").hide().find("input").val("");
    }
}

function validateStep(stepIndex) {
    if (stepIndex === 1) {
        if (!$('input[name=profileImageUrl]').val() && !$('input[name=uploadedProfileImageUrl]').val() ) {
            alert('이미지(회사 로고 등)를 첨부하시거나 샘플이미지를 선택해주세요.');
            return false;
        }
    } else if (stepIndex === 2) {
        if (!$('input[name=name]').val() || !$('input[name=nickname]').val()
            || !$('select[name=exposeType] option:selected').val()) {
            alert('필수 값을 입력 혹은 선택해 주세요.');
            return false;
        }

        if (authType !== 'EMAIL' && !$('input[name=email]').val()) {
            alert('필수 값을 입력 혹은 선택해 주세요.');
            return false;
        }

        if ($('input[name=name]').val().byteLength() > 20) {
            alert('이름은 20bytes 를 초과할 수 없습니다.');
            return;
        }
        if ($('input[name=nickname]').val().byteLength() > 30) {
            alert('닉네임은 30bytes 를 초과할 수 없습니다.');
            return;
        }
        var nickname = $('input[name=nickname]').val();
        if (!nicknameAvailableMap[nickname]) {
            alert('닉네임 중복체크를 해주세요');
            return;
        }
        if ('true' !== $('input[name=cellphoneCertified]').val()) {
            alert('휴대전화번호 인증이 필요합니다.');
            return false;
        }
    } else if (stepIndex === 3) {
        if ($('input[name=exposeSns]').is(':checked') && !$('input[name=homepageUrl]').val()) {
            alert('홈페이지 주소를 입력해주세요.');
            return false;
        }
    } else if (stepIndex === 4) {
        if ($('input[name=password]').val() || $('input[name=passwordConfirm]').val()) {
            if ($('input[name=password]').val() !== $('input[name=passwordConfirm]').val()) {
                alert('입력하신 비밀번호가 일치하지 않습니다.');
                return false;
            }

        }
    }

    return true;
}


function checkNicknameDuplicate() {
    var nickname = $('input[name=nickname]').val();
    if (!nickname || nickname.length < 2) {
        alert('닉네임을 2글자 이상 입력해 주세요');
        return;
    }
    $.ajax({
        method: 'GET',
        url: '/users/check?nickname=' + encodeURIComponent(nickname),
        cache: false,
        contentType: false,
        processData: false,
        success: function(result) {
            nicknameAvailableMap[nickname] = true;
            alert('사용가능한 닉네임 입니다.');
            $('#btn-check-nickname').css('visibility', 'hidden');
        },
        error: function(data, textStatus, error) {
            if (data.status === 409) {
                alert('이미 존재하는 닉네임 입니다.');
                return;
            }

            alert("서버와의 통신 중 문제가 발생하였습니다. code:"+data.status+"\n"+"message:"+data.responseText+"\n"+"error:"+error);
        }
    });
}

function countChar(val) {
    var content = $('textarea[name=clientInfo]').val();
    var len = content.length;
    if (len >= 1000) {
        $('textarea[name=clientInfo]').val(content.substring(0, 1000));
    } else {
        $('#textCnt').text(len.toLocaleString());
    }
}