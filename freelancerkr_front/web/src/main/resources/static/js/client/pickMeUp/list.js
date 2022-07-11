var listElm = document.querySelector('.pickMeUp');
var loadingElm = document.querySelector('.pickMeUp .loading');
var loading = false;

$(window).scroll(function() {
    if($(window).scrollTop() + $(window).height() >= $(document).height() - 40) {
        if (loading) return;
        loadMore();
    }
});

$(document).ready(function() {
    if (category1stId) {
        selectCategory1st($('[data-category-1st-id=' + category1stId + ']'), category1stId, true);
    }

    $('#directMarketSort').change(function() {
        var queryString = window.location.search;

        var url = '/view/pick-me-ups';
        if($(this).is(':checked')) {
            url += '?directMarketAvailable=true';
        } else {
            url += '?directMarketAvailable=false';
        }
        location.href = url;
    });

    init();

    if (afterPosting) {
        localStorage.setItem('afterPosting', "true");
    }

    if ('true' === localStorage.getItem('afterPosting')) {
        if ('true' !== localStorage.getItem('hideBottomPopup')) {
            $('.choice_pickmeup_infobar').show();
        }
    }
});

$(document).on('click', '.choice_pickmeup_close', function() {
    localStorage.setItem('hideBottomPopup', "true");
});

function init() {
    var currentPageNumber = parseInt($('input[name=currentPageNumber]').val());
    if (currentPageNumber > 0) {
        console.log(currentPageNumber);

        var url = '/pick-me-ups?pageNumber=0&pageSize=' + ((currentPageNumber + 1) * 24);
        if ($('#directMarketSort').is(':checked')) {
            url += '&directMarketAvailable=true';
        } else {
            url += '&directMarketAvailable=false';
        }

        if (category1stId) {
            url += '&category1stId=' + category1stId;
        }

        if (category2ndId) {
            url += '&category2ndId=' + category2ndId;
        }

        var keyword = $('input[name=keyword]').val();
        if (keyword) {
            url += '&keyword=' + keyword;
        }

        url += '&removeFirstPage=true';

        $(".moreResultBlock").load(url, function() {

            $('html, body').animate({
                scrollTop: $("#loadMoreBtn").offset().top
            }, 0);
            loading = false;
            loadingElm.style.visibility = 'hidden';

        });
        $(".moreResultBlock").removeClass('moreResultBlock');
        currentPage = currentPageNumber + 1;
        $('input[name=currentPageNumber]').val(currentPage);
    }
}

function loadMore() {
    if (loading) return;
    loading = true;
    currentPage = currentPage + 1;
    console.log('currentPage: ' + currentPage);
    var queryString = window.location.search;
    var url = '/pick-me-ups?pageNumber=' + (parseInt(currentPage));
    if ($('#directMarketSort').is(':checked')) {
        url += '&directMarketAvailable=true';
    } else {
        url += '&directMarketAvailable=false';
    }

    if (category1stId) {
        url += '&category1stId=' + category1stId;
    }

    if (category2ndId) {
        url += '&category2ndId=' + category2ndId;
    }

    var keyword = $('input[name=keyword]').val();
    if (keyword) {
        url += '&keyword=' + keyword;
    }

    // Show loading icon.
    loadingElm.style.visibility = '';

    $(".moreResultBlock").load(url, function () {
        loading = false;
        loadingElm.style.visibility = 'hidden';
    });
    $(".moreResultBlock").removeClass('moreResultBlock');
    $('input[name=currentPageNumber]').val(currentPage);
    // if (parseInt($('input[name=totalPages]').val()) === currentPage) {
    //     $('.detail-view-btn').hide();
    // }
}

function selectCategory1st(elem, id, blockReload) {
    var url = '/categories?parentCategoryId=' + id;
    if ($('#directMarketSort').is(':checked')) {
        url += '&directMarketAvailable=true';
    } else {
        url += '&directMarketAvailable=false';
    }
    $('.sub_navigation .category-1st-container li').removeClass('current');
    $(elem).addClass('current');

    category1stId = id;
    $.ajax({
        method: 'GET',
        url: url,
        cache: false,
        contentType: false,
        processData: false,
        success: function (result) {
            var $childCategoryContainer = $('.category-2nd-container');
            var $childCategoryContainerMobile = $('select.category-2nd-container');
            $childCategoryContainer.empty();
            $childCategoryContainerMobile.empty();
            $childCategoryContainer.append('<li id=""' + (category2ndId?'':'class="current"') + ' onclick="filterByCategory1st(' + id + ')" >전체</li>');
            $childCategoryContainerMobile.append('<option id=""' + (category2ndId?'':'class="current"') + ' >All</option>');
            for (var i = 0; i < result.length; i++) {
                $childCategoryContainer.append('<li class="' + ((result[i]['id']===category2ndId)?'current':'') + '" onclick="selectCategory2nd(this, ' + result[i]['id'] + ',' + id + ')" id="' + result[i]['id'] + '">' + result[i]["name"] + '</li>');
                $childCategoryContainerMobile.append('<option class="' + ((result[i]['id']===category2ndId)?'current':'') + '"  value="' + result[i]['id'] + '">' + result[i]["name"] + '</option>');
            }

            if (category2ndId) {
                $('select.category-2nd-container option[value=' + category2ndId + ']').prop('selected', true);
            }

            if (!blockReload) {
                filterByCategory1st(id);
            }
        },
        error: function (err, textStatus, xhr) {
            console.log(err);
        }
    });
}

function filterByAll() {
    var url = '/view/pick-me-ups';
    if ($('#directMarketSort').is(':checked')) {
        url += '?directMarketAvailable=true';
    } else {
        url += '?directMarketAvailable=false';
    }
    location.href=url;
}

function filterByCategory1st(id) {
    var url = '/view/pick-me-ups?category1stId=' + id;
    if ($('#directMarketSort').is(':checked')) {
        url += '&directMarketAvailable=true';
    } else {
        url += '&directMarketAvailable=false';
    }
    location.href = url;
}

function selectCategory2nd(elem, id, category1stId) {
    if (!category1stId) {
        category1stId = $('select[name=category1st] option:selected').val();
    }
    var url = '/view/pick-me-ups?category1stId=' + category1stId +'&category2ndId=' + id;
    $('.category-2nd-container li').removeClass('current');
    $(elem).addClass('current');
    if ($('#directMarketSort').is(':checked')) {
        url += '&directMarketAvailable=true';
    } else {
        url += '&directMarketAvailable=false';
    }
    location.href = url;
}

$(document).on('mouseover', '.filterCategory', function() {
   var id = $(this).data('category-1st-id');
    // filterByCategory1st(id);
    $.ajax({
        type: 'GET',
        url: '/categories?parentCategoryId=' + id,
        success: function(result) {
            var $childCategoryContainer = $('.category-2nd-container');
            var $childCategoryContainerMobile = $('select.category-2nd-container');
            $childCategoryContainer.empty();
            $childCategoryContainerMobile.empty();
            $childCategoryContainer.append('<li id=""' + (category2ndId?'':'class="current"') + ' onclick="filterByCategory1st(' + id + ')" >전체</li>');
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



// 프로젝트등록 마지막단계 팝업
$(document).ready(function(){
    $('#pick_start').click(function(){
        $('#last_step_project').hide();
        $('#choice_pickmeup_infobar').show();
    });
    $('.choice_pickmeup_close').click(function(){
        $('#choice_pickmeup_infobar').hide();
    });
});