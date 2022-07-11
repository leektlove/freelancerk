function countChar(val) {
    var content = $('textarea[name=description]').val();
    var len = content.length;
    if (len >= 1000) {
        $('textarea[name=description]').val(content.substring(0, 1000));
    } else {
        $('#textCnt').text(len.toLocaleString());
    }
}

function validateStep(stepIndex) {
    if (stepIndex === 1) {
        var selectedKeywordCount = selectedKeywords.length;
        if (selectedKeywords.length === 0) {
            selectedKeywordCount = $('input[name=selectedKeywordJson\\[\\]]').length;
        }

        if (!$('input[name=name]').val().length > 255) {
            alert('프로젝트 명은 255자를 초과할 수 없습니다.');
            return false;
            $('input[name=name]').focus();
            return false;
        }

        if (selectedKeywordCount === 0) {
            alert('프로젝트의 섹터와 키워드를 선택해주세요');
            return false;
        }

        if (selectedKeywordCount < 3) {
            alert('키워드를 3개 이상 선택 또는 입력해주세요. 많은 키워드를 선택할 수록 매칭 확률이 높아집니다. :)');
            return false;
        }

        if (!$('input[name=logoImageFile]').val() && (!$('input[name=modifiedLogoImage]').val() && !$('input[name=uploadedLogoImageFile]').val())) {
            alert('이미지(회사 로고 등)를 첨부하시거나 샘플이미지를 선택해주세요.');
            return false;
        }

        return true;
    } else if (stepIndex === 2) {
        var description = $('textarea[name=description]').val();
        description = description.replace(/&nbsp;/gi,'').replace(/\s/g, "");
        if (!$(description).text()) {
            alert('자세한 설명을 입력해주세요.');
            return false;
        }

    } else if (stepIndex === 3) {
        if ('PER_INCENTIVE' === $('select[name=payMean] option:selected').val()) {
            if (!$('input[name=defaultMoney]').val()) {
                alert('기본급을 입력해 주세요.');
                return false;
            }
        } else {
            var budgetType = $('input:radio[name=budgetType]:checked').val();
            if ('input' === budgetType) {
                if (!$('input[name=budgetFrom]').val() ||  !$('input[name=budgetTo]').val()) {
                    alert('입찰기준가격을 입력해 주세요.');
                    return false;
                }
            }
        }
    }

    return true;
}

function submit() {
    var name = $('input[name=name]').val();
    var expectedPeriod = $('select[name=expectedPeriod] option:selected').val();
    var expectedPeriodInput = $('input[name=expectedPeriodInput]').val();
    var payMean = $('select[name=payMean] option:selected').val();
    var workPlace = $('select[name=workPlace] option:selected').val();
    var numberOfPersons = $('select[name=number-of-persons] option:selected').val();
    var useEscrow = $('select[name=useEscrow] option:selected').val();
    var budgetFrom = $('input[name=budgetFrom]').val();
    var budgetTo = $('input[name=budgetTo]').val();
    var selectedKeywordCount = selectedKeywords.length;
    var description = $('textarea[name=description]').val();
    description = description.replace(/&nbsp;/gi,'').replace(/\s/g, "");

    if (budgetFrom) {
        budgetFrom = budgetFrom.replace(/\,/g,'');
        $('input[name=budgetFrom]').val(budgetFrom);
    }

    if (budgetTo) {
        budgetTo = budgetTo.replace(/\,/g,'');
        $('input[name=budgetTo]').val(budgetTo);
    }

    if (!name) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        $('input[name=name]').focus();
        return false;
    }

    if (!$(description).text()) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        $('textarea[name=description]').focus();
        return false;
    }

    if (!expectedPeriod) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        return false;
    }

    if ('DIRECT' === expectedPeriod && !expectedPeriodInput) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        return false;
    }

    if (!payMean) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        return false;
    }

    if (!workPlace) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        return false;
    }

    if (!numberOfPersons) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        return false;
    }

    if (!useEscrow) {
        alert('모든 항목이 입력되어야 다음으로 진행됩니다');
        return false;
    }

    if (selectedKeywordCount === 0) {
        alert('섹터와 관련 키워드를 등록해주세요');
        return false;
    }

    if (!confirm('임시보관함에 저장되었습니다. 다음단계로 진행됩니다.')) {
        return false;
    }

    var $inputContainer = $('.selected-keywords-container');
    $inputContainer.empty();

    for (var i = 0; i < selectedKeywords.length; i++) {
        $inputContainer.append('<input type="hidden" name="selectedKeywordJson[]" value=' + selectedKeywords[i] + '/>');
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonCategoryName[]" value="' + JSON.parse(selectedKeywords[i]).categoryName + '"/>');
        $inputContainer.append('<input type="hidden" name="selectedKeywordJsonKeyword[]" value="' + JSON.parse(selectedKeywords[i]).keyword + '"/>');
    }

    e.preventDefault();

    $.ajax({
        url: $('#fproject').attr('action'),
        type: 'POST',
        contentType : false,
        dataType: "JSON",
        cache: false,
        async:false,
        processData: false,
        data: new FormData($('#fproject')[0]),
        success: function(response) {
            if ('temp-save' === btnId) {
                alert('임시저장되었습니다.');
                location.href='/client/bid/autoSave';
                return;
            }
            location.href='/client/posting/projectOption?projectId=' + response.data.id;
        },
        error: function() {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }

    });
    return false;
}

function init() {
    var useEscrow = $('select[name=useEscrow] option:selected').val();
    if (useEscrow === 'true') {
        $('.refund-info-container').show();
    } else {
        $('.refund-info-container').hide();
    }

    var meetUpPlace = $('select[name=workPlace] option:selected').val();
    if ('OFF_LINE' === meetUpPlace) {
        $('.meet-up-place-container').show();
    } else {
        $('.meet-up-place-container').hide();
    }

    var expectedPeriod = $('select[name=expectedPeriod] option:selected').val();
    if ('DIRECT' === expectedPeriod) {
        $('.expectedPeriodInputContainer').show();
    } else {
        $('.expectedPeriodInputContainer').hide();
    }

    if (1*$('input[name=budgetFrom]').val() === 0 && 1*$('input[name=budgetTo]').val() === 0 && modifyMode) {
        $('input[name=budgetType][value=dontknow]').prop('checked', true);
    }
    
    $('textarea[name=description]').summernote({
        lang: 'ko-KR',
        height: 500,
        // placeholder: 'write here...',
        fontNames: ['Nanum Gothic','Apple SD Gothic Neo','sans-serif'],
        toolbar: [
            // [groupName, [list of button]]
            ['style', ['bold', 'italic','strikethrough', 'underline']],
            ['fontname',['Nanum Gothic','Apple SD Gothic Neo','sans-serif']],
            ['fontsize',['fontsize']],
            ['color',['#000']],
            ['para', ['paragraph']],
            ['height',['height']],
            ['insert', ['link', 'picture', 'video']],
            ['table',['table']],
            ['hr',['hr']],
            ['view', ['undo','redo']],
        ],
        callbacks: {
            onImageUpload: function(files, editor, welEditable) {
                for (var i = files.length - 1; i >= 0; i--) {
                    sendFile(files[i], this);
                }
            }
        }
    });

    if (!modifyMode) {
        var HTMLstring = '<span class="font-weight-bold">1. 서비스개요</span><br><br><br><br><br><br><span class="font-weight-bold">2. 구체적인 작업 내용 설명<br><br><br><br><br><br><span class="font-weight-bold">3. 참고 사항</span><br><br><br><br><br><br>';

        $('textarea[name=description]').summernote('code', HTMLstring);
    }
}

function sendFile(file, el) {
    var form_data = new FormData();
    form_data.append('file', file);
    $.ajax({
        data: form_data,
        type: "POST",
        url: '/files',
        cache: false,
        contentType: false,
        enctype: 'multipart/form-data',
        processData: false,
        success: function(img_name) {
            $(el).summernote('editor.insertImage', img_name);
        }
    });
}

$(document).ready(function() {

    $('input[name=logoImageFile]').on('change', function() {
        readURL($(this)[0], $('#logo-img'), null);
    });

    $('input[name=logoImageFile]').on('click', function() {
        $(this).val('');
        $('input[name=modifiedLogoImage]').val('');
    });

    $('#btn-upload-logo-image').click(function() {
        $('#logoImageFile').trigger('click');
    })

    $('#postingStartAt').on('changeDate', function(ev){
        $(this).datepicker('hide');
    });

    $('#postingEndAt').on('changeDate', function(ev){
        $(this).datepicker('hide');
    });

    init();

    $('input[name=projectDescriptionFile]').on('change', function() {
        $('#attachedFileCancelUpload').show();
        $('.real-file-input').show();
        $('.fake-file-input').hide();
    });

    $('input[name=projectDescriptionFile]').on('click', function() {
        $(this).val('');
    });

    $('#attachedFileCancelUpload').click(function() {
        $('input[name=projectDescriptionFile]').val('');
        $(this).hide();
        $('.real-file-input').show();
        $('.fake-file-input').hide();
    });

    $('#fake-file-btn').click(function() {
        $('input[name=projectDescriptionFile]').trigger('click');
    });

    $( "#preview_pop" ).on('shown.bs.modal', function(){
        $('#preview-box #preview-title').html($('input[name=name]').val());
        if ($('select[name=expectedPeriod] option:selected').val()) {
            $('#preview-box #preview-period').html($('select[name=expectedPeriod] option:selected').text());
        }
        if ($('select[name=numberOfPersons] option:selected').val()) {
            $('#preview-box #preview-numberOfPersons').html($('select[name=numberOfPersons] option:selected').text());
        }
        if ($('select[name=workPlace] option:selected').val()) {
            $('#preview-box #preview-workPlace').html($('select[name=workPlace] option:selected').text());
        }
        if ($('select[name=payMean] option:selected').val()) {
            $('#preview-box #preview-payMean').html($('select[name=payMean] option:selected').text());
        }
        if ($('select[name=payCriteria] option:selected').val()) {
            $('#preview-box #preview-payCriteria').html($('select[name=payCriteria] option:selected').text());
        }

        var useEscrow = $('select[name=useEscrow] option:selected').val();
        console.log(useEscrow);
        if (useEscrow === 'true') {
            $('#preview-escrow-badge').show();
        } else {
            $('#preview-escrow-badge').hide();
        }

        $('#preview-box #preview-budget').html($('input[name=budgetFrom]').val() + '만원 ~ ' + $('input[name=budgetTo]').val() + '만원');

        if ('PER_INCENTIVE' === $('select[name=payMean] option:selected').val()) {
            $('#preview-box #preview-incentive-area').show();
            $('#preview-box #preview-defaultMoney').html($('input[name=defaultMoney]').val());
            $('#preview-box #preview-incentive').html($('input[name=incentiveFrom]').val());
            $('#preview-box #preview-payCriteria-container').hide();
        } else if ('PER_UNIT' === $('select[name=payMean] option:selected').val()) {
            $('#preview-box #preview-incentive-area').hide();
            $('#preview-box #preview-payCriteria-container').show();
        } else {
            $('#preview-box #preview-incentive-area').hide();
            $('#preview-box #preview-payCriteria-container').hide();
        }
    });

    var budgetType = $('input:radio[name=budgetType]').val();
    if ('input' === budgetType) {
        // $('.budget-container input').prop('readonly', false);
        $('.budget-incentive-container input').prop('readonly', false);
        $('.budget-general-container input').prop('readonly', false);
    } else {
        // $('.budget-container input').prop('readonly', true);
        $('.budget-incentive-container input').prop('readonly', true);
        $('.budget-general-container input').prop('readonly', true);
    }

    if (!modifyMode && $('input[name=selectedKeywordJson\\[\\]]').length === 0) {

        $('select[name=payMean]').val('PER_UNIT');
        $('.pay-criteria-container').show();
        $('select[name=workPlace]').val('ON_LINE_AFTER_MEET');
        //

        $('#postingStartAt').datepicker({
            defaultDate: moment().zone('+0900')
        }).datepicker('setDate', moment().format('YYYY-MM-DD'));
        $('#postingEndAt').datepicker({
            changeYear: true,
            startDate: '+1D',
            endDate: '+2M',
            language: "ko"
        }).datepicker('setDate', moment().add(2, 'M').format('YYYY-MM-DD'));
    } else {
        $('#postingStartAt').datepicker('setDate', postingStartDate);
        $('#postingEndAt').datepicker('setDate', postingEndDate);
        $('#postingEndAt').datepicker({
            changeYear: true,
            startDate: postingStartDate,
            language: "ko"
        });

        if ($('input:radio[name=budgetType]:checked').val() === 'dontknow') {
            $('input[name=budgetFrom]').val('0');
            $('input[name=budgetTo]').val('0');
            $('.budget-incentive-container input').prop('readonly', true);
            $('.budget-general-container input').prop('readonly', true);
        }
    }

    $('#fproject').submit(function(e) {
        var name = $('input[name=name]').val();
        var description = $('textarea[name=description]').val();
        description = description.replace(/&nbsp;/gi,'').replace(/\s/g, "");
        var expectedPeriod = $('select[name=expectedPeriod] option:selected').val();
        var expectedPeriodInput = $('input[name=expectedPeriodInput]').val();
        var payMean = $('select[name=payMean] option:selected').val();
        var workPlace = $('select[name=workPlace] option:selected').val();
        var numberOfPersons = $('select[name=numberOfPersons] option:selected').val();
        var useEscrow = $('select[name=useEscrow] option:selected').val();
        var budgetFrom = $('input[name=budgetFrom]').val();
        var budgetTo = $('input[name=budgetTo]').val();
        var selectedKeywordCount = selectedKeywords.length;
        if (selectedKeywords.length === 0) {
            selectedKeywordCount = $('input[name=selectedKeywordJson\\[\\]]').length;
        }

        if (budgetFrom) {
            budgetFrom = budgetFrom.replace(/\,/g,'');
            $('input[name=budgetFrom]').val(budgetFrom);
        }

        if (budgetTo) {
            budgetTo = budgetTo.replace(/\,/g,'');
            $('input[name=budgetTo]').val(budgetTo);
        }

        if (!name) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            $('input[name=name]').focus();
            return false;
        }

        if (!$(description).text()) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            $('textarea[name=description]').focus();
            return false;
        }

        if (!expectedPeriod) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            return false;
        }

        if ('DIRECT' === expectedPeriod && !expectedPeriodInput) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            return false;
        }

        if (!payMean) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            return false;
        }

        if (!workPlace) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            return false;
        }

        if (!numberOfPersons) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            return false;
        }

        if (!useEscrow) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다');
            return false;
        }

        if (selectedKeywordCount === 0) {
            alert('모든 항목이 입력되어야 다음으로 진행됩니다.');
            return false;
        }


        if (useEscrow && JSON.parse(useEscrow)) {
            var bankForReceivingPaymentId = $('select[name=bankForReceivingPayment] option:selected').val();
            var bankAccountForReceivingPayment = $('input[name=bankAccountForReceivingPayment]').val();
            var bankAccountName = $('input[name=bankAccountName]').val();

            // if (!bankForReceivingPaymentId || !bankAccountForReceivingPayment || !bankAccountName) {
            //     alert('예치 후 환불관련 정보를 입력해 주세요');
            //     return false;
            // }
        }

        var postingStartAt = $('#postingStartAt').val();
        var postingEndAt = $('#postingEndAt').val();
        $('input[name=postingStartAt]').val(postingStartAt);
        $('input[name=postingEndAt]').val(postingEndAt);

        var $inputContainer = $('.selected-keywords-container');
        $inputContainer.empty();

        for (var i = 0; i < selectedKeywords.length; i++) {
            $inputContainer.append('<input type="hidden" name="selectedKeywordJsonId[]" value="' + JSON.parse(selectedKeywords[i]).id + '"/>');
            $inputContainer.append('<input type="hidden" name="selectedKeywordJsonCategoryName[]" value="' + JSON.parse(selectedKeywords[i]).categoryName + '"/>');
            $inputContainer.append('<input type="hidden" name="selectedKeywordJsonKeyword[]" value="' + JSON.parse(selectedKeywords[i]).keyword + '"/>');
        }

        e.preventDefault();

        $.ajax({
            url: $('#fproject').attr('action'),
            type: 'POST',
            contentType : false,
            dataType: "JSON",
            cache: false,
            async:false,
            processData: false,
            data: new FormData($('#fproject')[0]),
            success: function(response) {
                if ('tempSave' === $('input[name=postType]').val()) {
                    alert('임시저장되었습니다.');
                    location.href='/client/bid/autoSave';
                    return;
                } else if ('modify' === $('input[name=postType]').val()) {
                    alert('성공적으로 수정이 완료되었습니다. 포스팅 옵션에 대한 추가 적용에 대해서는 [결제관리]에서 이용해주십시오.');
                    location.href='/client/bid/processingList';
                    return;
                }
                location.href='/client/posting/projectOption?projectId=' + response.data.id;
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }

        });

        return false;
    });

    $("form input[type=submit]").click(function() {
        $("input[type=submit]", $(this).parents("form")).removeAttr("clicked");
        $(this).attr("clicked", "true");
    });

    $('select[name=workPlace]').on('change', function() {
        var value = $(this).val();
        if ('OFF_LINE' === value) {
            $('.meet-up-place-container').show();
        } else {
            $('.meet-up-place-container').hide();
        }
    });

    $('select[name=payMean]').change(function() {
        if ($(this).val() === 'PER_WORD') {
            $('.budgetUnit').text('원');
        } else {
            $('.budgetUnit').text('만원');
        }

        if ($(this).val() === 'PER_UNIT') {
            $('.pay-criteria-container').show();
        } else {
            $('.pay-criteria-container').hide();
        }

        if ($(this).val() === 'PER_INCENTIVE') {
            $('.budget-incentive-container').show();
            $('.budget-general-container').hide();
        } else {
            $('.budget-incentive-container').hide();
            $('.budget-general-container').show();
        }
    })

    $('select[name=expectedPeriod]').change(function() {
        if ($(this).val() === 'DIRECT') {
            $('.expectedPeriodInputContainer').show();
        } else {
            $('.expectedPeriodInputContainer').hide();
        }
    });

    $('select[name=useEscrow]').change(function() {
        if ($(this).val() === 'true') {
            $('.refund-info-container').show();
        } else {
            $('.refund-info-container').hide();
        }
    });

    // $('input[name=defaultMoney], input[name=incentiveFrom], input[name=incentiveTo]').keydown(function() {
    //     if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
    //         // Allow: Ctrl/cmd+A
    //         (e.keyCode == 65 && (e.ctrlKey === true || e.metaKey === true)) ||
    //         // Allow: Ctrl/cmd+C
    //         (e.keyCode == 67 && (e.ctrlKey === true || e.metaKey === true)) ||
    //         // Allow: Ctrl/cmd+X
    //         (e.keyCode == 88 && (e.ctrlKey === true || e.metaKey === true)) ||
    //         // Allow: home, end, left, right
    //         (e.keyCode >= 35 && e.keyCode <= 39)) {
    //         // var it happen, don't do anything
    //         return;
    //     }
    //     // Ensure that it is a number and stop the keypress
    //     if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
    //         alert('숫자만 입력가능합니다.');
    //     }
    // })

    $('input:radio[name=budgetType]').change(function() {
        var value = $(this).val();
        if ('input' === value) {
            $('.budget-incentive-container input').prop('readonly', false);
            $('.budget-general-container input').prop('readonly', false);
        } else {
            if (!confirm('프리랜서는 명확한 입찰기준가격을 선호합니다. 계속진행하시겠습니까?')) {
                $('#budgetTypeInput').prop("checked", true);
                return false;
            }
            $('input[name=budgetFrom]').val('0');
            $('input[name=budgetTo]').val('0');
            $('.budget-incentive-container input').prop('readonly', true);
            $('.budget-general-container input').prop('readonly', true);
            // $('.budget-container input').prop('readonly', true);
        }
    });


    $('input[name=budgetFrom], input[name=budgetTo]').focusin(function() {
        if ($(this).val() === '0') {
            $(this).val('');
        }
    });

    $('#btn-preview').click(function() {
        // todo
    });

    $('#btn-temp-save').click(function() {
        $('input[name=postType]').val('tempSave');
        $('#fproject').submit();
    });

    $('#btn-modify').click(function() {
        var projectId = $('input[name=projectId]').val();
        $('input[name=postType]').val('modify');
        $('#fproject').attr('action', '/projects/' + projectId + '/modifications');
        $('#fproject').submit();
    });

    $('#btn-post').click(function() {
        $('input[name=postType]').val('post');
        $('#fproject').submit();
    });

    $('#sampleLogoModal .pms_c').click(function() {
        $('input[name=modifiedLogoImage]').val($(this).attr('src'));
        $('#logo-img').attr('src', $(this).attr('src'));
        $('#sampleLogoModal').modal('hide');
    });

    $('#btn-cancel').click(function() {
        if (!confirm('취소하시겠습니끼?')) {
            return;
        }

        location.href = '/client/mypage';
    })

    getKeywords();

    $(document).on('click', '.add-keyword-btn', function() {
        var dic = {};
        var categoryId = $(this).closest('.divChildCategory').data('category-id');
        var categoryName = $(this).closest('.divChildCategory').data('category-name');
        var keyword = $(this).closest('.divChildCategory').find('input[name=keyword]').val();
        if (!keyword) return;
        if (keyword.indexOf('#') !== -1) {
            alert('문자만 입력할 수 있어요:)');
            return;
        }
        dic['id'] = categoryId;
        dic['categoryName'] = categoryName;
        dic['keyword'] = keyword;
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
        console.log(categoryNameKeywordsDic);
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
    })
});

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

$(document).on('change', 'select[name=workPlace]', function() {
    if ('OFF_LINE' === $(this).val()) {
        alert('특수직종(통역 등)을 제외하고, 대부분의 프리랜서는 상주근무 보다 원격근무를 선호하는 경향이 있습니다.');
        return;
    }
});

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