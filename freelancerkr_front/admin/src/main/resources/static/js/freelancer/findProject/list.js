$(document).ready(function () {

    // 찜하기 버튼
    $('.btnLike').on("click", function (e) {
        e.preventDefault();
        if(!guard()) {
            return;
        }
        if (loginAsClient) {
            alert('클라이언트는 프로젝트 찜하기를 사용하실 수 없습니다');
            return;
        }

        if (!userInfoInput) {
            if (confirm('프로필 업로드 완료 후 가능합니다.')) {
                location.href='/freelancer/profile/modify';
                return;
            }
            return;
        }
        var projectId = $(this).attr('id');

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

    /**
     * 초기 설정
     */
    // 체크된 키워드 버튼들 체크
    $('.btnKeyword').each(function () {
        var keywords = $('#txtKeywords').text();
        var keyword = $(this).find('span').text();
        if (keywords.indexOf(keyword) > -1) {
            $(this).addClass('tag-button-on');
        }
    });

    // 키워드 버튼
    $('.btnKeyword').on("click", function (e) {
        var keyword = $(this).find('span').text();
        console.log(keyword);

        var keywords = $('#txtKeywords').text();
        if (keywords.indexOf(keyword) > -1) {
            // remove
            keywords = keywords.replace(keyword + "|", "");
            $(this).removeClass('tag-button-on');
        } else {
            // add
            keywords = keywords + keyword + "|";
            $(this).addClass('tag-button-on');
        }
        $('#txtKeywords').text(keywords);
    });

    // 규모 버튼
    $('#btnSortByBudgetDescNull').on("click", function () {
        console.log("btnSortByBudgetDescNull");
        $('#inputBudgetDesc').val(true);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
        // $('#searchForm').submit(function(e) {
        //    event.preventDefault();
        //     console.log("submit!!");
        // });
    });

    $('#btnSortByBudgetDescTrue').on("click", function () {
        console.log("btnSortByBudgetDescTrue");
        $('#inputBudgetDesc').val(false);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);

    });

    $('#btnSortByBudgetDescFalse').on("click", function () {
        console.log("btnSortByBudgetDescFalse");
        $('#inputBudgetDesc').val(true);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    // 기간 버튼
    $('#btnSortByExtectedPeriodDescNull').on("click", function () {
        console.log("btnSortByExtectedPeriodDescNull");
        $('#inputExtectedPeriodDesc').val(true);
        $('#inputBudgetDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    $('#btnSortByExtectedPeriodDescTrue').on("click", function () {
        console.log("btnSortByExtectedPeriodDescTrue");
        $('#inputExtectedPeriodDesc').val(false);
        $('#inputBudgetDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    $('#btnSortByExtectedPeriodDescFalse').on("click", function () {
        console.log("btnSortByExtectedPeriodDescFalse");
        $('#inputExtectedPeriodDesc').val(true);
        $('#inputBudgetDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    // 마감일 버튼
    $('#btnSortByPostingEndDateDescNull').on("click", function () {
        console.log("btnSortByPostingEndDateDescNull");
        $('#inputPostingEndDateDesc').val(true);
        $('#inputBudgetDesc').val(null);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    $('#btnSortByPostingEndDateDescTrue').on("click", function () {
        console.log("btnSortByPostingEndDateDescTrue");
        $('#inputPostingEndDateDesc').val(false);
        $('#inputBudgetDesc').val(null);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    $('#btnSortByPostingEndDateDescFalse').on("click", function () {
        console.log("btnSortByPostingEndDateDescFalse");
        $('#inputPostingEndDateDesc').val(true);
        $('#inputBudgetDesc').val(null);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputMatchingKeywordsDesc').val(null);
    });

    // 키워드 일치 버튼
    $('#btnSortByMatchingKeywordsDescNull').on("click", function () {
        console.log("btnSortByMatchingKeywordsDescNull");
        $('#inputMatchingKeywordsDesc').val(true);
        $('#inputBudgetDesc').val(null);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
    });

    $('#btnSortByMatchingKeywordsDescTrue').on("click", function () {
        console.log("btnSortByMatchingKeywordsDescTrue");
        $('#inputMatchingKeywordsDesc').val(false);
        $('#inputBudgetDesc').val(null);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
    });

    $('#btnSortByMatchingKeywordsDescFalse').on("click", function () {
        console.log("btnSortByMatchingKeywordsDescFalse");
        $('#inputMatchingKeywordsDesc').val(true);
        $('#inputBudgetDesc').val(null);
        $('#inputExtectedPeriodDesc').val(null);
        $('#inputPostingEndDateDesc').val(null);
    });

    // 자세히 보기 버튼
    $('.applyModal').on("click", function (e) {
        e.preventDefault();
        if (!userInfoInput) {
            if (confirm('프로필 업로드 완료 후 가능합니다.')) {
                location.href='/freelancer/profile/modify';
                return;
            }
            return;
        }
        var userId = $(this).parent().find('.clientId').val();
        var projectId = $(this).parent().find('.projectId').val();
        var projectTitle = $(this).closest('.item-container').find('.project-item-subject').text();
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
                $('.popUpProjectTitle').text(projectTitle);
                $('#popUpUserName').text(result['corporateName']);
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

        if (!amount || amount === 0) {
            alert('금액을 입력해 주세요');
            return;
        }
        var formData = new FormData();
        formData.append('projectId', projectId);
        formData.append('amount', removeComma(amount));
        formData.append('bidType', 'PROJECT_BID');
        formData.append('bidStatus', 'APPLY');

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

    // 페이지네이션 처리
    $('.btnPremiumPageNumber').on("click", function (e) {
        var pageNum = $(this).text();
        console.log("pageNum : ", pageNum);
        $('#inputPremiumPageNumber').val(pageNum - 1);
        $("#submit").trigger('click');
    });

    $('.btnSponsoredPageNumber').on("click", function (e) {
        var pageNum = $(this).text();
        console.log("pageNum : ", pageNum);
        $('#inputSponsoredPageNumber').val(pageNum - 1);
        $("#submit").trigger('click');
    });

    $('select[name=projectType]').change(function() {
        if ($(this).val() === 'CONTEST') {
            $('select[name=category] option[data-valid-in-contest=false]').hide();
        } else {
            $('select[name=category] option[data-valid-in-contest=false]').show();
        }
    })
});

// $(document).on('click', '#submit', function() {
//     $('#searchForm').submit();
// });

function toProjectDetail(projectId, denyable) {
    if(!guard()) {
        return;
    }

    if (loginAsClient) {
        location.href = '/client/project/' + projectId + '/details';
    } else {
        if (denyable) {
            alert('이미 해당 클라이언트에게 제안을 받으셨습니다. 제안받은 프로젝트에서 확인해보세요!');
            location.href='/freelancer/bid/suggestedList';
            return;
        }
        location.href = '/freelancer/projects/' + projectId + '/details';
    }

}

$(document).on('change', 'select[name=category], select[name=projectType], select[name=sortBy]', function() {
   var url = '/freelancer/findProject/list';

   var category = $('select[name=category] option:selected').val();
   var projectType = $('select[name=projectType] option:selected').val();
   var sortBy = $('select[name=sortBy] option:selected').val();

   url += '?category=' + category;
   url += '&projectType=' + projectType;
   url += '&sortBy=' + sortBy;

   location.href = url;
});

function searchByTag(keyword) {
    $('input[name=keyword]').val(keyword);
    $('#submit').trigger('click');
}

function guard() {
    if (!loggedIn) {
        location.href = '/auth/login';
        return false;
    }
    return true
}

function toContestDetail(id, clientUserId, denyable) {
    if(!guard()) {
        return;
    }

    if (loginAsClient) {
        location.href='/client/contest/' + id + '/details';
    } else {
        if (JSON.parse(denyable)) {
            alert('이미 해당 클라이언트에게 제안받은 프로젝트입니다! 이 프로젝트를 제안받은 내역에서 확인해보세요!');
            location.href='/freelancer/bid/suggestedList';
            return;
        }
        location.href = '/freelancer/contests/' + id + '/details';
    }
}