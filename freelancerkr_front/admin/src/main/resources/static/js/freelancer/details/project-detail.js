$(document).ready(function () {
    $(".comment-view").scrollTop($(".comment-view").scrollHeight);

    $('.applyModal').on("click", function (e) {
        if (!isLoggedIn) {
            alert('로그인 후 이용해 주세요.');
            location.href = '/auth/login?redirectUrl=' + encodeURIComponent(('https://freelancerk.com/freelancer/projects/' + $('#projectId').val() + '/details'));
            return;
        }

        if (!userInfoInput) {
            e.stopPropagation();
            alert('프로필 입력 후 참여하실 수 있습니다. 클라이언트는 참여자의 프로필과 포트폴리오를 꼼꼼히 검토한 후 낙찰자를 선정합니다');
            location.href='/freelancer/profile/modify';
            return false;
        }

        if (useNdaIp && !$('input#checkNdaIp').is(':checked')) {
            $('#nda-ip-option-info').modal('show');
            return;
        }

        var userId = $('input[name=clientId]').val();
        var projectId = $('#projectId').val();
        var projectTitle = $('#project-title').text();
        $('#popUpProjectId').val(projectId);

        $.ajax({
            method: 'GET',
            url: '/findUser?userId=' + userId,
            cache: false,
            contentType: false,
            processData: false,
            success: function (result) {
                console.log("result >> ", result);
                // for (var i = 0; i < result.length; i++) {
                //     var mainCategoryId = result[i]["id"];
                //     $("#selectMainCategory").append(new Option(result[i]["name"], mainCategoryId));
                // }
                $('#popUpProjectTitle').text(projectTitle);
                $('#popUpUserName').text(result['exposeName']);
                if (result['exposeEmail']) {
                    $('#popUpUserEmail').text(result['email']);
                } else {
                    $('#popUpUserEmail').text('비공개');
                }
                if (result['exposeCellphone']) {
                    $('#popUpMobile').text(result['cellphone']);
                } else {
                    $('#popUpMobile').text('비공개');
                }

                if (result['exposeSns']) {
                    $('.sns-container').show();
                    $('.sns-container .kakaoId').text(result['kakaoId']);
                    $('.sns-container .facebookId').text(result['facebookId']);
                    $('.sns-container .nateOnId').text(result['nateOnId']);
                    $('.sns-container .naverId').text(result['naverId']);
                    $('.sns-container .telegramId').text(result['telegramId']);
                } else {
                    $('.sns-container').hide();
                    $('.sns-container .kakaoId').text('');
                    $('.sns-container .facebookId').text('');
                    $('.sns-container .nateOnId').text('');
                    $('.sns-container .naverId').text('');
                    $('.sns-container .telegramId').text('');
                }

                var target = $(".applyModal").attr('data-target-custom');
                $(target).modal('show');
            },
            error: function (err, textStatus, xhr) {
                // console.log(err);
            }
        });
    });

    // 팝업 제출
    $('.btnApplyBid').on("click", function (e) {
        e.preventDefault();
        if (!userInfoInput) {
            if (confirm('프로필 업로드 완료 후 가능합니다.')) {
                location.href='/freelancer/profile/modify';
                return;
            }
            return;
        }
        if (!confirm(' [주의] 입찰에 참여하면 클라이언트는 귀하의 프로필과 포트폴리오를 검토할 것입니다. 현재의 프로필과 포트폴리오로 입찰에 참여하시겠습니까?')) {
            if (!confirm('마이페이지로 이동합니다.')) {
                return;
            }
            location.href='/freelancer/profile/index';
            return;
        }
        var projectId = $('#popUpProjectId').val();
        var amount = $('#bidAmount').val();

        var formData = new FormData();
        formData.append('projectId', projectId);
        formData.append('amount', (removeComma(amount)));
        formData.append('bidType', 'PROJECT_BID');
        formData.append('bidStatus', 'APPLY');

        if (!amount || amount === 0) {
            alert('입찰 금액을 입력해 주세요.');
            return;
        }

        $.ajax({
            method: 'POST',
            url: '/applyBid',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert('입찰에 참여했습니다.');
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });


    // 찜하기 버튼
    $('.btnLike').on("click", function (e) {
        if (!isLoggedIn) {
            alert('로그인 후 이용해 주세요.');
            location.href = '/auth/login';
            return;
        }
        e.preventDefault();
        var projectId = $('#projectId').val();

        var formData = new FormData();
        formData.append('projectId', projectId);

        $.ajax({
            method: 'POST',
            url: '/addWishList',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert(result['message']);
                // alert('나의 프로젝트 > 찜한 프로젝트에서 확인하실 수 있습니다.');
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    // 메시지 등록
    $('#btnReply').on("click", function(e) {
        if (!isLoggedIn) {
            alert('로그인 후 이용해 주세요.');
            location.href = '/auth/login';
            return;
        }
        e.preventDefault();
        if (bidCompleted && !projectInProgress) {
            alert('입찰이 종료되어 메시지 전송이 불가능합니다.');
            return false;
        }
        var projectId = $('#projectId').val();
        var contents = $('textarea[name=contents]').val();
        if (!contents) {
            alert(' 메시지를 입력해주세요.');
            return;
        }
        if (contents.length > 1000) {
            alert('메시지는 1,000 자 이상 입력할 수 없습니다.');
        }
        var formData = new FormData();
        formData.append('content', contents);

        $.ajax({
            method: 'POST',
            url: '/projects/' + projectId + '/comments',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function() {
                // alert('나의 프로젝트 > 찜한 프로젝트에서 확인하실 수 있습니다.');
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    $('#decline_approval').on('shown.bs.modal', function(e) {
        var denyReason = $(e.relatedTarget).data('deny-reason');
        $('#denyReason').text(denyReason);
    })



    $('input[name=projectContractFile]').change(function() {

        var formData = new FormData();
        formData.append('file', $(this)[0].files[0]);

        var projectId = $('input[name=id]').val();
        $.ajax('/projects/' + projectId + '/contract-files', {
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.responseCode === 'SUCCESS') {
                    var item = res.data;
                    $('#projectContractFileContainer').append('<div><a href="' + item['fileUrl'] + '">' + item['fileName'] + '</a> <i class="fa fa-window-close cursor projectContractFileDeleteBtn" data-id="' + item['id'] + '" aria-hidden="true"></i></div>');
                }
            },

            error: function () {
                alert('파일 저장에 실패했습니다.');
            },

            complete: function () {
            },
        });
    });

    $(document).on('click', '.projectContractFileDeleteBtn', function() {
        if (!confirm('정말 삭제하시겠습니까?')) {
            return;
        }
        var id = $(this).data('id');
        var $elem = $(this).closest('div');
        $.ajax({
            url: '/project-contract-files/' + id,
            type: 'DELETE',
            success: function() {
                $($elem).remove();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        })
    });

    $('#bid').on('shown.bs.modal', function(event) {
        if (projectBudget) {
            $('#bid .projectBudget').text('프로젝트 ' + projectBudget);
            $('#bid .popUpProjectTitle').text(projectTitle);
        }
    });

    if (notPostedStatusAlarm) {
        alert('진행 중인 프로젝트가 아닙니다.');
    }
});

$(document).on('click', '.btn-delete', function() {
   var id = $(this).data('id');
   if (!confirm('삭제하시겠습니끼?')) {
       return;
   }

   $.ajax({
       type: 'DELETE',
       url: '/project-comments/' + id,
       success: function(response) {
           if ('SUCCESS' === response.responseCode) {
               alert('요청에 성공했습니다.');
               location.reload();
           } else {
               alert('요청에 실패했습니다.');
           }
       },
       error: function(error) {
           alert('요청에 실패했습니다.');
           console.error(error);
       }
   })
});

function denyProjectProposition(projectPropositionId) {
    if (!confirm('제안을 거절하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/project-propositions/' + projectPropositionId + '/deny',
        success: function() {
            alert('성공적으로 요청되었습니다.');
            location.href = '/freelancer/bid/suggestedList';
        },
        error: function() {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        }

    })
}