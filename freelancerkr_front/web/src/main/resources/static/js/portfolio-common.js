$(document).ready(function () {

    $('#btnReply').click(function () {
        if (!isLoggedIn) {
            alert('로그인 후 이용해주세요.');
            return;
        }

        var pickMeUpId = $('input[name=pickMeUpId]').val();
        var content = $('textarea[name=content]').val();

        if (!confirm('등록하시겠습니까?')) {
            return;
        }

        if (!content) {
            alert('내용을 입력해 주세요.');
            return;
        }

        var formData = new FormData();
        formData.append('content', content);

        $.ajax({
            type: 'POST',
            url: '/pick-me-ups/' + pickMeUpId + '/comments',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function (data) {
                location.reload();
            },
            error: function () {
                alert('요청에 실패했습니다.');
            }
        })
    })

    $('textarea[name=content]').click(function (e) {
        if (!isLoggedIn) {
            e.stopPropagation();
            alert('로그인 후 이용해주세요.');
            return true;
        }
    });

    //픽미업상세 사이드바 영역
    $('.push_menu').click(function() {
        if($('.wrapper').hasClass('active')){
            $('.wrapper').removeClass('active');
            $('#container').css({'margin-left':'0px','transition':'margin-left 0.5s'});
            $('#comment').show().css('transition','show 0.5s');
            $('.count').css({'display': 'block'});
            $('.btn-body').css('width','calc(100% - 80px)');
            $('#arrow').html('<i class="fas fa-chevron-right"></i>');
        }else {
            $('.wrapper').addClass('active');
            $('#comment').hide().css('transition','hide 0.5s');
            $('#container').css({'margin-left':'100px','transition':'margin-left 0.5s'});
            $('.count').css({'display': 'none'});
            $('.btn-body').css('width','100%');
            $('#arrow').html('<i class="fas fa-chevron-left"></i>');
        }
    })

    var clipboard = new ClipboardJS('.btnShare');
    clipboard.on('success', function (e) {
        alert('클립보드에 복사되었습니다');
        e.clearSelection();
    });

});


function openProposeModal(id) {
    if (!isLoggedIn) {
        alert('[클라이언트] 로그인 후 [포스팅]을 하시면 무제한 요청이 가능해요.');
        return;
    }

    if (!loginAsClient) {
        alert('포스팅을 완료한 [클라이언트]가 이용하는 버튼입니다:)');
        return;
    }
    $('#suggest_bid input[name=pickMeUpId]').val(id);
    $('#suggest_bid').modal();
}

function saveDirectDeal(id) {
    if (!isLoggedIn) {
        alert('[클라이언트] 로그인 후 이용하실 수 있어요 :)');
        return true;
    }

    if (!loginAsClient) {
        alert('[클라이언트]가 이용하는 버튼입니다 :)');
        return;
    }

    $.ajax({
        url: '/pick-me-ups/' + id + '/direct-deals',
        type: 'POST',
        processData: false,
        cache: false,
        contentType: false,
        success: function () {
            if (confirm(' 나의 직거래마켓에 담겼습니다.나의 직거래마켓으로 이동할까요?')) {
                location.href = '/client/directMarket/index';
                return;
            }
            location.reload();
        },
        error: function (jqXHR) {
            if (jqXHR.status === 409) {
                alert('이미 담겨있는 직거래 포트폴리오입니다.');
                return;
            }
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    })
}

function propose(preventRefresh) {
    var projectId = $('select[name=proposition-project] option:selected').val();
    var pickMeUpId = $('input[name=pickMeUpId]').val();
    $.ajax({
        url: '/projects/' + projectId + '/propositions?pickMeUpId=' + pickMeUpId,
        type: 'POST',
        processData: false,
        cache: false,
        contentType: false,
        success: function (response) {
            if ('SUCCESS' === response.responseCode) {
                alert('성공적으로 요청되었습니다.');
                if (preventRefresh) {
                    return;
                }
                location.reload();
            } else {
                alert(response.message);
            }
        },
        error: function (jqXHR) {
            if (jqXHR.status === 409) {
                alert('이미 제안하신 프리랜서의 포트폴리오입니다.');
                return;
            }
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    })
}

function countChar(val) {
    var content = $('textarea[name=content]').val();
    var len = content.length;
    if (len >= 100) {
        $('textarea[name=content]').val(content.substring(0, 100));
    } else {
        $('#textCnt').text(len.toLocaleString());
    }
}

function deleteComment(id) {
    if (!confirm('정말 삭제하시겠습니까?')) {
        return;
    }
    $.ajax({
        url: '/pick-me-ups/comments/' + id,
        type: 'DELETE',
        success: function (data) {
            if (data.responseCode === 'SUCCESS') {
                location.reload();
            } else {
                alert(data.message);
            }
        },
        error: function () {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    });
}

function toPickMeUpDetail(url, loggedIn, directDeal) {
    // if (!loggedIn && directDeal) {
    //     alert('직거래 포트폴리오는 로그인 후 열람하실 수 있습니다. 1분이면 회원가입이 가능합니다. :)');
    //     return;
    // }
    location.href = url;
}

function toggleLikesItem(id) {
    if(!guard()) {
        return;
    }

    var liked = JSON.parse($('input[name=liked]').val());
    if (liked) {
        $.ajax({
            url: '/pick-me-ups/' + id + '/likes',
            type: 'DELETE',
            success: function (response) {
                if ('SUCCESS' === response.responseCode) {
                    var likeCount = parseInt($('input[name=likeCount]').val());
                    likeCount = likeCount - 1;
                    $('.likeCount').text(likeCount.toLocaleString());
                    $('input[name=liked]').val(false);
                    $('.fa-heart').css('color', 'inherit');
                    $('input[name=likeCount]').val(likeCount);
                } else if ('FAIL' === response.responseCode) {
                    alert(response.message);
                }
            },
            error: function(error) {
                console.error(error);
                alert('요청 중 문제가 발생했습니다.');
            }
        })
    } else {

        $.ajax({
            url: '/pick-me-ups/' + id + '/likes',
            type: 'POST',
            success: function (response) {
                if ('SUCCESS' === response.responseCode) {
                    var likeCount = parseInt($('input[name=likeCount]').val());
                    likeCount = likeCount + 1;
                    $('.likeCount').text(likeCount.toLocaleString());
                    $('input[name=liked]').val(true);
                    $('.fa-heart').css('color', 'rgb(277,97,89)');
                    $('input[name=likeCount]').val(likeCount);
                } else if ('FAIL' === response.responseCode) {
                    alert(response.message);
                }
            },
            error: function(error) {
                console.error(error);
                alert('요청 중 문제가 발생했습니다.');
            }
        })
    }
}

function guard() {
    if (!isLoggedIn) {
        location.href = '/auth/login';
        return false;
    }
    return true
}

