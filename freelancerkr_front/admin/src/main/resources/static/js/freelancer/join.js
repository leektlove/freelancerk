//체크박스 개수제한
$(document).ready(function() {
    $("input[name=expertiseSector]:checkbox").change(function() {// 체크박스들이 변경됬을때
        var cnt = '3';
        if( cnt==$("input[name=expertiseSector]:checkbox:checked").length ) {
            $("input[name=expertiseSector]:checkbox:not(:checked)").attr("disabled", "disabled");
        } else {
            $("input[name=expertiseSector]:checkbox").removeAttr("disabled");
        }
    });

    $("input[name=expertiseSector]:checkbox").on('click', function() {
        if ( $(this).prop('checked') ) {
            $(this).parents().eq(2).addClass("is_selected");
        } else {
            $(this).parents().eq(2).removeClass("is_selected");
        }
    });
    $('input[name=password]').keyup(function() {
        if ($(this).val().length < 4) {
            $('#password-txt').hide();
            return;
        }
        var passwordChecker = zxcvbn($(this).val());
        console.log(passwordChecker.score);
        $('#password-txt').html(passwordStrengthTxt(passwordChecker.score));
        $('#password-txt').show();
    })

    $('input[name=password_confirm]').keyup(function() {
        if ($(this).val().length < 4) {
            $('#password-confirm-txt').hide();
            return;
        }
        var passwordChecker = zxcvbn($(this).val());
        console.log(passwordChecker.score);
        $('#password-confirm-txt').html(passwordStrengthTxt(passwordChecker.score));
        $('#password-confirm-txt').show();
    })



    $('input[name=expertiseSector\\[\\]]').change(function() {
        if (this.checked) {
            $(this).siblings('label').css('opacity', '.3');
        } else {
            $(this).siblings('label').css('opacity', '1');
        }
    });

    $(document).on('click', '.add-keyword-btn', function() {
        var dic = {};
        var categoryId = $(this).closest('.divChildCategory').data('category-id');
        var categoryName = $(this).closest('.divChildCategory').data('category-name');
        var keyword = $(this).closest('.divChildCategory').find('input[name=keyword]').val();
        if (!keyword) return;
        dic['id'] = categoryId;
        dic['categoryName'] = categoryName;
        dic['keyword'] = keyword;
        if (!categoryName || !categoryId || !keyword) {
            return;
        }
        if(!checkKeywordContain(keyword)) {
            alert('이미 선택된 키워드 입니다');
            return;
        }
        var $container = $(this).closest('.divChildCategory').find('.selected-keyword-container');
        selectedKeywords.push(JSON.stringify(dic));
        $container.append("<span class='badge badge-pill keyword_badge'>" +
            "<span class='tag_word'>" + keyword + "</span>" +
            "<span class='tag_del ml-2' data-keyword='" + keyword + "'>X</span>" +
            "</span>");
        $(this).closest('.divChildCategory').find('input[name=keyword]').val('');
    });

    $(document).on('click', '.tag_del', function() {
        var dic = {};
        var keyword = $(this).data('keyword');
        var categoryId = $(this).closest('.divChildCategory').data('category-id');
        var categoryName = $(this).closest('.divChildCategory').data('category-name');

        dic['id'] = categoryId;
        dic['categoryName'] = categoryName;
        dic['keyword'] = keyword;

        selectedKeywords.remove(JSON.stringify(dic));
        $(this).closest('.badge-pill').remove();
    });

    $(document).on('click', '.del_selected_tag', function() {
        var dic = {};
        var keyword = $(this).data('keyword');
        var categoryId = $(this).data('category-id');
        var categoryName = $(this).data('category-name');

        dic['id'] = categoryId;
        dic['categoryName'] = categoryName;
        dic['keyword'] = keyword;

        selectedKeywords.remove(JSON.stringify(dic));
        $(this).closest('.badge-pill').remove();
    });

    $(document).on('click', '.popular-keyword-btn', function() {
        var categoryId = $(this).closest('.divChildCategory').data('category-id');
        var $container = $(this).closest('.divChildCategory').find('.popular-keyword-container');
        $.ajax({
            method: 'GET',
            url: '/categories/' + categoryId + '/popular-keywords',
            cache: false,
            contentType: false,
            processData: false,
            success: function (keywords) {
                $container.empty();
                for (var i in keywords) {
                    if (!keywords[i].name) continue;
                    var keywordElem = "<span><span class='btn text-secondary btn-xs btn-keyword' data-keyword='" + keywords[i].name + "'>#<span></span>" + keywords[i].name + "</span></span>";
                    $container.append(keywordElem);
                }
                $container.show();

            }
        });
    });

    $(document).on('click', '.recommended-keyword-btn', function() {
        var categoryId = $(this).closest('.divChildCategory').data('category-id');
        var recommendedKeywordStrs = $(this).closest('.divChildCategory').data('recommended-keywords');
        var $container = $(this).closest('.divChildCategory').find('.recommended-keyword-container');
        var recommendedKeywords = recommendedKeywordStrs.split(',');
        $container.empty();
        var i = 0;
        for (var i = 0; i < recommendedKeywords.length; i++) {
            var keyword = recommendedKeywords[i];
            if (!keyword) continue;
            var keywordElem = ("<span><span class='btn text-primary btn-xs btn-keyword' data-keyword='" + keyword + "'>#<span></span>" + keyword + "</span></span>");
            $container.append(keywordElem);
        }
        $container.show();
    });

    $(document).on('click', '.btn-keyword', function() {
        var categoryId = $(this).closest('.divChildCategory').data('category-id');
        var categoryName = $(this).closest('.divChildCategory').data('category-name');
        var keyword = $(this).data('keyword');
        if (!keyword) return;
        var dic = {};
        dic['id'] = categoryId;
        dic['categoryName'] = categoryName;
        dic['keyword'] = keyword;

        if(!checkKeywordContain(keyword)) {
            alert('이미 선택된 키워드 입니다');
            return;
        }
        var $container = $(this).closest('.divChildCategory').find('.selected-keyword-container');
        selectedKeywords.push(JSON.stringify(dic));
        $container.append(" <span class='badge badge-pill keyword_badge'>" +
            "<span class='tag_word'>" + keyword + "</span>" +
            "<span class='tag_del ml-2' data-keyword='" + keyword + "'>X</span>" +
            "</span>");
    });

    $(document).on('click', '.chkBoxChildCategory', function () {
        var divChildCategory = $(this).parent().parent().parent().find('.wrap').find('.divChildCategory');
        var categoryId = divChildCategory.data('category-id');

        if ($(this).is(':checked')) {
            divChildCategory.append(
                '<div class="d-flex justify-content-between mt-2">' +
                '<div>' +
                '<span class="btn btn-secondary btn-outline key_show align-middle popular-keyword-btn">인기키워드 <span>▼</span></span>' +
                '<span class="btn btn-primary btn-outline key_show align-middle recommended-keyword-btn ml-2">참고키워드 <span>▼</span></span>' +
                '</div>' +
                '<div class="input-group ml-2" style="max-width:300px;">' +
                '<input type="text" class="form-control" value="" name="keyword" maxlength="20" placeholder="직접입력" style="border-width:2px;border-right:0">' +
                '<span class="input-group-append">'+
                '<span class="btn btn-light btn-outline key_add align-middle add-keyword-btn">키워드추가 <span>▼</span></span>' +
                '</span>'+
                '</div>' +
                '</div>' +
                '<div>' +
                '<div style="padding-top:8px; display: none" class="popular-keyword-container"></div>' +
                '<div style="padding-top:8px; display: none" class="recommended-keyword-container"></div>' +
                '</div>' +
                '<div style="padding-top:8px;" class="selected-keyword-container">' +
                '</div>'

            ).trigger("create");
        } else {
            for (var i = 0 ; i < selectedKeywords.length; i++) {
                var dic = JSON.parse(selectedKeywords[i]);
                if (dic['id'] === categoryId) {
                    selectedKeywords.remove(JSON.stringify(dic));
                }
            }
            divChildCategory.children().remove();

        }
    });

    $('#key_add_btn').click(function(){
        var $container = $('.selected-keywords-container');
        var categoryNameKeywordsDic = {};
        var categoryIdKeywordsDic = {};
        for (var i = 0 ; i < selectedKeywords.length; i++) {
            var dic = JSON.parse(selectedKeywords[i]);
            if(categoryNameKeywordsDic[dic['categoryName']]) {
            } else {
                categoryNameKeywordsDic[dic['categoryName']] = [];
                categoryIdKeywordsDic[dic['categoryName']] = [];
            }
            categoryNameKeywordsDic[dic['categoryName']].push(dic['keyword']);
            categoryIdKeywordsDic[dic['categoryName']].push(dic['id']);
        }

        $container.empty();
        for (var i in categoryNameKeywordsDic) {
            var subKeywordsElem = '';
            for (var j = 0; j < categoryNameKeywordsDic[i].length; j++) {
                if(!categoryNameKeywordsDic[i][j]) continue;
                subKeywordsElem += '<span class="badge badge-pill keyword_badge"><span class="tag_word">' + categoryNameKeywordsDic[i][j] + '</span>' +
                    '<span class="tag_del del_selected_tag ml-2" data-keyword="' + categoryNameKeywordsDic[i][j] + '" data-category-name="' + i + '"' +
                    'data-category-id="' + categoryIdKeywordsDic[i][j] + '">X</span>' + '</span>';
            }

            $container.append('<div class="tag_list">' +
                '<div>' + i + '</div>' +
                '<div class="tag_sub_list">' +
                subKeywordsElem +
                '</div>' +
                '</div>' +
                '</div>'
            );
        }
        $('#key_add_pop').modal('hide');
    });

    init();
});

function init() {
    getKeywords();
}

function joinFPlus() {
    // e.preventDefault();

    if (!$('input[name=email]').val()) {
        alert('이메일을 입력해주세요');
        return false;
    }

    var email = $('input[name=email]').val();
    if (!validateEmail(email)) {
        alert(' 유효한 이메일주소가 아닙니다. 다시 한번 확인해주세요.');
        return false;
    }

    if (!$('input[name=password]').val()) {
        alert('비밀번호를 입력해주세요');
        return false;
    }

    if ($('input[name=password]').val() !== $('input[name=password_confirm]').val()) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }

    if ($('input[name=password]').val().length < 4) {
        alert('비밀번호를 4자이상 입력해주세요');
        return false;
    }

    if (!$('input[name=name]').val()) {
        alert('이름을 입력해주세요');
        return false;
    }

    if (!$('input[name=cellphone]').val()) {
        alert('휴대전화번호를 입력해주세요');
        return false;
    }

    // if (!$('input:checkbox[name=agree1]').is(':checked') || !$('input:checkbox[name=agree2]').is(':checked') || !$('input:checkbox[name=agree3]').is(':checked')) {
    //     alert('이용약관등에 대한 동의가 필요합니다');
    //     return false;
    // }

    if (!$('input:checkbox[name=agree]').is(':checked')) {
        alert('이용약관등에 대한 동의가 필요합니다');
        return false;
    }

    var $inputContainer = $('.selected-keywords-container');
    $inputContainer.empty();
    for (var i = 0; i < selectedKeywords.length; i++) {
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonId[]" value="' + JSON.parse(selectedKeywords[i]).id + '"/>');
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonCategoryName[]" value="' + JSON.parse(selectedKeywords[i]).categoryName + '"/>');
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonKeyword[]" value="' + JSON.parse(selectedKeywords[i]).keyword + '"/>');
    }

    $.ajax({
        type: 'GET',
        url: '/users/check?email=' + $('input[name=email]').val() + '&fpUser='+ $('input[name=fpUser]').val(),
        success: function() {

            try {
                window.dataLayer = window.dataLayer || [];
                window.dataLayer.push({'pageCategory': 'freelancer'});
            } catch (e) {
                console.error(e);
            }

            $('#form').submit();
        },
        error: function(error) {
            if (error.status === 409) {
                alert('이미 회원가입이 완료된 이메일 주소입니다. ');
                return false;
            }
            alert('가입 중 문제가 발생하였습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    });

    return false;
}

function join() {
    // e.preventDefault();

    if (selectedKeywords.length === 0 && $('input[name=selectedKeywordJson\\[\\]]').length === 0) {
        alert('나의 섹터와 관련 키워드를 등록해주세요(필수)');
        return;
    }

    if (selectedKeywords.length < 3) {
        alert('키워드를 3개 이상 선택 또는 입력해주세요. 많은 키워드를 선택할 수록 매칭 확률이 높아집니다. :)');
        return;
    }

    if (!$('input[name=email]').val()) {
        alert('이메일을 입력해주세요');
        return false;
    }

    var email = $('input[name=email]').val();
    if (!validateEmail(email)) {
        alert(' 유효한 이메일주소가 아닙니다. 다시 한번 확인해주세요.');
        return false;
    }

    if (!$('input[name=password]').val()) {
        alert('비밀번호를 입력해주세요');
        return false;
    }

    if ($('input[name=password]').val() !== $('input[name=password_confirm]').val()) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }

    if ($('input[name=password]').val().length < 4) {
        alert('비밀번호를 4자이상 입력해주세요');
        return false;
    }

    if (!$('input[name=name]').val()) {
        alert('이름을 입력해주세요');
        return false;
    }

    if (!$('input[name=cellphone]').val()) {
        alert('휴대전화번호를 입력해주세요');
        return false;
    }

    // if (!$('input:checkbox[name=agree1]').is(':checked') || !$('input:checkbox[name=agree2]').is(':checked') || !$('input:checkbox[name=agree3]').is(':checked')) {
    //     alert('이용약관등에 대한 동의가 필요합니다');
    //     return false;
    // }

    if (!$('input:checkbox[name=agree]').is(':checked')) {
        alert('이용약관등에 대한 동의가 필요합니다');
        return false;
    }

    var $inputContainer = $('.selected-keywords-container');
    $inputContainer.empty();
    for (var i = 0; i < selectedKeywords.length; i++) {
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonId[]" value="' + JSON.parse(selectedKeywords[i]).id + '"/>');
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonCategoryName[]" value="' + JSON.parse(selectedKeywords[i]).categoryName + '"/>');
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonKeyword[]" value="' + JSON.parse(selectedKeywords[i]).keyword + '"/>');
    }

    $.ajax({
        type: 'GET',
        url: '/users/check?email=' + $('input[name=email]').val() + '&fpUser='+ $('input[name=fpUser]').val(),
         success: function() {

            try {
                window.dataLayer = window.dataLayer || [];
                window.dataLayer.push({'pageCategory': 'freelancer'});
            } catch (e) {
                console.error(e);
            }

            $('#form').submit();
        },
        error: function(error) {
            if (error.status === 409) {
                alert('이미 회원가입이 완료된 이메일 주소입니다. ');
                return false;
            }
            alert('가입 중 문제가 발생하였습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    });

    return false;
}

function getKeywords() {
    $.ajax({
        method: 'GET',
        url: '/allCategories',
        cache: false,
        contentType: false,
        processData: false,
        success: function (result) {
            console.log(result);

            for (var i = 0; i < result.length; i++) {
                var parentCategoryId = result[i]["parentCategoryId"];
                var parentCategoryName = result[i]["parentCategoryName"];
                var childCategories = result[i]["childCategories"];

                for (var j = 0; j < childCategories.length; j++) {
                    if (j == 0) {
                        $('.sectorTable').append(
                            '<div class="categoryStyle col-12" rowspan="' + childCategories.length + '">' +
                            '<label>' +
                            '<span>' + parentCategoryName + '</span>' +
                            '</label>' +
                            '</div>' +
                            '<div class="parentCategory col-12">' +
                            '<div>' +
                            '<label class="mb-0">' +
                            '<input type="checkbox" value="" class="chkBoxChildCategory" style="margin-bottom:0.2rem;">' +
                            '<input type="hidden" value="">' +
                            '<span class="ml-2">' + childCategories[j]["name"] + '</span>' +
                            '</label>' +
                            '</div>' +
                            '<div class="wrap">' +
                            '<div class="divChildCategory" data-category-id="' + childCategories[j]['id'] + '" data-category-name="' + childCategories[j]['name'] + '" data-recommended-keywords="' + childCategories[j]['tags'] + '">' +

                            '</div>' +
                            '</div>' +
                            '</div>').trigger("create");
                    } else {
                        $('.sectorTable').append(
                            '<div class="parentCategory col-12">' +
                            '<div>' +
                            '<label class="mb-0">' +
                            '<input type="checkbox" value="" class="chkBoxChildCategory" >' +
                            '<input type="hidden" value="">' +
                            '<span class="ml-2">' + childCategories[j]["name"] + '</span>' +
                            '</label>' +
                            '</div>' +
                            '<div class="wrap">' +
                            '<div class="divChildCategory" data-category-id="' + childCategories[j]['id'] + '" data-category-name="' + childCategories[j]['name'] + '" data-recommended-keywords="' + childCategories[j]['tags'] + '">' +

                            '</div>' +
                            '</div>' +
                            '</div>').trigger("create");
                    }

                }
            }
        },
        error: function (err, textStatus, xhr) {
            // console.log(err);
        }
    });
}

function checkKeywordContain(keyword) {
    keyword = keyword.trim();
    for (var i = 0 ; i < selectedKeywords.length; i++) {
        var dic = JSON.parse(selectedKeywords[i]);
        if (dic['keyword'] === keyword) {
            return false;
        }
    }

    return true;
}

// $('input:checkbox[name=expertiseSector\\[\\]]:checked');