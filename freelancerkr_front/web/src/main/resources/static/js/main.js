var hitRecorded = false;
var clickRecorded = false;

$.fn.inView = function(){
    //Window Object
    var win = $(window);
    //Object to Check
    obj = $(this);
    //the top Scroll Position in the page
    var scrollPosition = win.scrollTop();
    //the end of the visible area in the page, starting from the scroll position
    var visibleArea = win.scrollTop() + win.height();
    //the end of the object to check
    var objEndPos = (obj.offset().top + obj.outerHeight());
    return(visibleArea >= objEndPos && scrollPosition <= objEndPos ? true : false)
};

$(document).ready(function() {
    // var notshowpopup = localStorage.getItem('notshowpopup');
    // if (!notshowpopup) {
    //     $('#imageModal').modal();
    // }

    if (category1stId) {
        // $('[data-category-1st-id=' + category1stId + ']').trigger('click');
        selectCategory1st($('[data-category-1st-id=' + category1stId + ']'), category1stId, true);
    }

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

    // Custom Video
    $('.play-video-custom').magnificPopup({
        type: 'iframe',
        mainClass: 'mfp-fade',
        removalDelay: 160,
        preloader: false,
        fixedContentPos: false
    });
});

$(document).on('change', 'input[name=notshowpopup]', function() {
    if ($(this).is(':checked')) {
        localStorage.setItem('notshowpopup', true);
    } else {
        localStorage.setItem('notshowpopup', false);
    }
});

$(document).on('click', '.wantHireBtn', function() {
    if (!loggedIn) {
        alert('[클라이언트]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        location.href = '/auth/loginClient';
        return;
    }
    if (!loginAsClient) {
        alert('[클라이언트]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        return;
    }

    location.href = '/infoPosting';
});

$(document).on('click', '.wantWorkBtn', function() {
    if (!loggedIn) {
        alert('[프리랜서]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        location.href = '/auth/loginFreelancer';
        return;
    }

    if (loginAsClient) {
        alert('[프리랜서]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        return;
    }
    location.href = '/freelancer/findProject/list';
});

function selectCategory1st(elem, id, blockReload) {
    if (!id) {
        filterByAll();
        return;
    }
    $('.sub_navigation .category-1st-container li').removeClass('current');
    $(elem).addClass('current');
    $.ajax({
        method: 'GET',
        url: '/categories?parentCategoryId=' + id,
        cache: false,
        contentType: false,
        processData: false,
        success: function (result) {
            var $childCategoryContainer = $('ul.category-2nd-container');
            var $childCategoryContainerMobile = $('select.category-2nd-container');
            $childCategoryContainer.empty();
            $childCategoryContainerMobile.empty();
            $childCategoryContainer.append('<li id=""' + (category2ndId?'':'class="current"') + ' onclick="filterByCategory1st(' + id + ')" >All</li>');
            $childCategoryContainerMobile.append('<option id=""' + (category2ndId?'':'class="current"') + ' >All</option>');
            for (var i = 0; i < result.length; i++) {
                $childCategoryContainer.append('<li class="' + ((result[i]['id']===category2ndId)?'current':'') + '" onclick="selectCategory2nd(this, ' + result[i]['id'] + ',' + id + ')" id="' + result[i]['id'] + '">' + result[i]["name"] + '</li>');
                $childCategoryContainerMobile.append('<option class="' + ((result[i]['id']===category2ndId)?'current':'') + '"  value="' + result[i]['id'] + '">' + result[i]["name"] + '</option>');
            }

            if (category2ndId) {
                $('select.category-2nd-container option[value=' + category2ndId + ']').prop('selected', true);
            }

            if (!blockReload) {
                location.href='/main?category1stId=' + id;
            }
        },
        error: function (err, textStatus, xhr) {
            console.log(err);
        }
    });
}

$(document).on('mouseover', '.filterCategory', function() {
    var id = $(this).data('category-1st-id');
    // filterByCategory1st(id);
    $.ajax({
        type: 'GET',
        url: '/categories?parentCategoryId=' + id,
        success: function(result) {
            var $childCategoryContainer = $('ul.category-2nd-container');
            var $childCategoryContainerMobile = $('select.category-2nd-container');
            $childCategoryContainer.empty();
            $childCategoryContainerMobile.empty();
            $childCategoryContainer.append('<li id=""' + (category2ndId?'':'class="current"') + ' onclick="filterByCategory1st(' + id + ')" >All</li>');
            $childCategoryContainerMobile.append('<option id=""' + (category2ndId?'':'class="current"') + ' >All</option>');
            for (var i = 0; i < result.length; i++) {
                $childCategoryContainer.append('<li class="' + ((result[i]['id']===category2ndId)?'current':'') + '" onclick="selectCategory2nd(this, ' + result[i]['id'] + ',' + id + ')" id="' + result[i]['id'] + '">' + result[i]["name"] + '</li>');
                $childCategoryContainerMobile.append('<option class="' + ((result[i]['id']===category2ndId)?'current':'') + '"  value="' + result[i]['id'] + '">' + result[i]["name"] + '</option>');
            }

            if (category2ndId) {
                $('select.category-2nd-container option[value=' + category2ndId + ']').prop('selected', true);
            }

        },
        error: function(error) {
            console.error(error);
        }

    });
});

function filterByAll() {
    location.href='/main';
}

function filterByCategory1st(id) {
    location.href='/view/pick-me-ups?category1stId=' + id;
}

function selectCategory2nd(elem, id, category1stId) {
    $('.category-2nd-container li').removeClass('current');
    $(elem).addClass('current');
    if (!category1stId) {
        category1stId = $('select.category-1st-container').val();
    }
    location.href='/view/pick-me-ups?category1stId=' + category1stId +'&category2ndId=' + id;
}

function toPortfolio() {
    location.href = '/view/pick-me-ups';
}

function toProject() {
    if (loginAsClient) {
        location.href = '/client/profile/modify?after-redirect=%2Fclient%2Fposting%2Fproject&type=PROJECT';
    } else {
        location.href = '/freelancer/findProject/list';
    }
}

function toContest() {
    guard();
    if (loginAsClient) {
        location.href = '/client/profile/modify?after-redirect=%2Fclient%2Fposting%2Fcontest&type=CONTEST';
    } else {
        location.href = '/freelancer/findProject/list';
    }
}

function guard() {
    if (!loggedIn) {
        location.href = '/auth/login';
        return false;
    }
    return true
}

function toProjectDetail(projectId, postingClientId, existsByBid, denyable) {
    // if(!guard()) {
    //     increaseProjectHit(projectId);
    //     return;
    // }

    if (!loginAsClient && denyable) {
        alert('이미 해당 클라이언트에게 제안을 받으셨습니다. 제안받은 프로젝트에서 확인해보세요!');
        location.href='/freelancer/bid/suggestedList';
        return;
    }

    if (!loginAsClient && existsByBid) {
        if (confirm('이미 입찰에 참여하신 프로젝트입니다. [참여중인 입찰]로 이동할까요? ')) {
            location.href = '/freelancer/bid/onGoingList';
            return;
        }
    }

    if (loginAsClient && postingClientId === userId) {
        alert('직접 등록하신 프로젝트입니다. 진행중인 입찰로 이동합니다.');
        location.href = '/client/bid/processingList';
        return;
    }
    if (loginAsClient) {
        location.href = '/client/project/' + projectId + '/details';
    } else {
        location.href = '/freelancer/projects/' + projectId + '/details';
    }

}

function toContestDetail(id, postingClientId, existsByBid) {

    if (!loginAsClient && existsByBid) {
        if (confirm('이미 입찰에 참여하신 컨테스트 입니다. [참여중인 입찰]로 이동할까요? ')) {
            location.href = '/freelancer/bid/onGoingList';
            return;
        }
    }

    if (loginAsClient && postingClientId === userId) {
        alert('직접 등록하신 컨테스트입니다. 진행중인 입찰로 이동합니다.');
        location.href = '/client/bid/processingList';
        return;
    }

    if (loginAsClient) {
        location.href='/client/contest/' + id + '/details';
    } else {
        location.href = '/freelancer/contests/' + id + '/details';
    }
}

function toProjectPosting() {
    if(!loggedIn) {
        alert('[클라이언트]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        return;
    }

    if (!loginAsClient) {
        alert('[클라이언트]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        return;
    }

    alert('인력파견을 업으로 하는 용역업체 등은 프로젝트 포스팅이 불가하며, 해당 포스트는 예고없이 삭제될 수 있습니다.');

    if (userInfoInput) {
        location.href='/client/posting/project';
    } else {
        location.href='/client/profile/modify?after-redirect=%2Fclient%2Fposting%2Fproject&type=PROJECT';
    }
}
function toContestPosting() {
    if(!loggedIn) {
        alert('[클라이언트]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        return;
    }

    if (!loginAsClient) {
        alert('[클라이언트]로 로그인 후 이용해주세요. 회원가입은 1분도 안걸려요. :)');
        return;
    }

    if (userInfoInput) {
        location.href='/client/posting/contest';
    } else {
        location.href='/client/profile/modify?after-redirect=%2Fclient%2Fposting%2Fcontest&type=CONTEST';
    }
}

function increaseProjectHit(projectId) {
    $.ajax({
        type: 'POST',
        url: '/projects/' + projectId + '/hits',
        success: function() {
            console.log('success');
        },
        error: function() {
            console.log('error');
        }
    })
}

$(window).scroll(function() {
    if (hitRecorded || !$('.ad').inView()) {
        return;
    }
    hitRecorded = true;

    increaseAdvertisementHit($('.ad').data('id'))
});

$(document).on('click', '.ad-link', function() {
    if (clickRecorded) {
        return;
    }

    clickRecorded = true;

   increaseAdvertisementClicks($(this).closest('.ad').data('id'));
   window.open($(this).data('link-url'));
});

function increaseAdvertisementHit(adId) {
    $.ajax({
        type: 'POST',
        url: '/advertisements/' + adId + '/hits',
        success: function() {
            console.log('success');
        },
        error: function() {
            console.log('error');
        }
    })
}

function increaseAdvertisementClicks(adId) {
    $.ajax({
        type: 'POST',
        url: '/advertisements/' + adId + '/clicks',
        success: function() {
            console.log('success');
        },
        error: function() {
            console.log('error');
        }
    })
}


