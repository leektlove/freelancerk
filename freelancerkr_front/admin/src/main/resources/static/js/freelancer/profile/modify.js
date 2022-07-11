var careerBtnId = 0;
var careerList;
$(document).ready(function () {
    init();

    $('.pms_c').click(function() {
        $('input[name=profileImageUrl]').val($(this).attr('src'));
        $('.slim-result img.in').attr('src', $(this).attr('src'));
        $('.slim-result img.in').css('opacity', 1);
    });

    $('#checkBoxExposeEmail').click(function() {
        var checkedExposeCellphone = $('#checkBoxExposeCellphone').is(":checked");
        var checkedExposeSns = $('#checkBoxExposeSns').is(":checked");

        if (!checkedExposeCellphone && !checkedExposeSns && !$(this).is(":checked")) {
            alert('한가지 이상 반드시 노출하셔야 합니다.');
            $(this).prop('checked', true);
        }

        if ($(this).is(':checked')) {
            $("input[name=email]").prop('disabled', false);
        } else {
            $("input[name=email]").prop('disabled', true);
        }
    });

    $('#checkBoxExposeCellphone').click(function() {
        var checkBoxExposeEmail = $('#checkBoxExposeEmail').is(":checked");
        var checkedExposeSns = $('#checkBoxExposeSns').is(":checked");

        if (!checkBoxExposeEmail && !checkedExposeSns && !$(this).is(":checked")) {
            alert('한가지 이상 반드시 노출하셔야 합니다.');
            $('#checkBoxExposeEmail').prop('checked', true);
        }

        if ($(this).is(':checked')) {
            $("input[name=cellphone]").prop('disabled', false);
        } else {
            $("input[name=cellphone]").prop('disabled', true);
        }
    });

    $('#checkBoxExposeSns').click(function() {
        var checkBoxExposeEmail = $('#checkBoxExposeEmail').is(":checked");
        var checkBoxExposeCellphone = $('#checkBoxExposeCellphone').is(":checked");

        // if ($(this).is(':checked')) {
        //     $(".cm_display_1_3_con").show();
        // } else {
        //     $(".cm_display_1_3_con").hide();
        // }
        if (!checkBoxExposeEmail && !checkBoxExposeCellphone && !$(this).is(":checked")) {
            alert('한가지 이상 반드시 노출하셔야 합니다.');
            $('#checkBoxExposeEmail').prop('checked', true);
        }
    });

    // 프로필 이미지 미리보기
    $('#btnUploadProfileImage').on('click', function () {
        // console.log("openFileInput");
        $("#inputProfileImageFile").trigger('click');
    });

    $('#inputProfileImageFile').change(function (e) {
        var fr = new FileReader();
        fr.onload = function () {
            $('#secImagePreivew').attr('src', fr.result);
        };
        fr.readAsDataURL(e.target.files[0]);
    });

    // 닉네임 중복체크
    $('#btnNickNameCheck').click(function (e) {
    });

    // 휴대전화번호 확인
    $('#btnGetCellPhoneCheck').click(function (e) {

    });

    // 비밀번호 확인
    $('#inputPassword').change(function (e) {
        // $('inputRepassword')
    });

    $('select[name=businessType]').change(function() {
        var businessType = $(this).val();
        if ('INDIVIDUAL' === businessType) {
            $('.indiv-info-container').show();
            $('.company-info-container').hide();
            $('input[name=taxType][value=COLLECTION_IN_ADVANCE]').attr('checked', 'checked');
        } else if ('CORP_BUSINESS' === businessType) {
            $('.indiv-info-container').hide();
            $('.company-info-container').show();
            $('input[name=taxType][value=VAT]').attr('checked', 'checked');
        } else {
            $('.indiv-info-container').show();
            $('.company-info-container').show();
        }
    });

    //file change
    $("#fm_license_file").change(function(){
        $("#fm_license_file_cancel").show();
    });
    $("#fm_license_file_cancel").click(function(){
        var file = $("#fm_license_file");
        file.replaceWith( file = file.clone( true ) );
        file.val("");
        $("#fm_license_file_cancel").hide();
    });

    $('input[name=aboutMeFile]').change(function() {
        $('#cancel-resume-upload').show();
    });

    $('#cancel-resume-upload').click(function() {
        $('input[name=aboutMeFile]').val('');
        $(this).hide();
    })

    $('#btnShowMySector').click(function (e) {
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

    $("#btn-check-nickname").click(function() {
        checkNicknameDuplicate();
    });

    $('input[name=nickname]').on('keyup', function() {
        $('#btn-check-nickname').css('visibility', 'visible');
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

    // 보유 기술
    var skillList = $('#inputUserSkillList').val();
    if (skillList) {
        skillList = JSON.parse(skillList);
        for (var i = 0; i < skillList.length; i++) {
            $('#ulUserSkillList').append(
                '<tr>' +
                    '<td class="pl-3">' +
                        '<span class="spanSkillName">' + skillList[i]['skillName'] + '</span>' +

                    '</td>' +
                    '<td class="text-center">' +
                        '<span class="spanSkillLevel">' + skillList[i]['skillLevel'] + '</span>' +
                    '</td>' +
                    '<td class="text-center">' +
                        '<button class="btnSkillDelete btn btn-light btn-sm" type="button">삭제</button>' +
                    '</td>' +
                '</tr>'
            );
        }
    }

    $('#btnAddSkill').click(function (e) {
        var skillName = $('#inputSkillName').val();
        var skillLevel = $('#selectSkillLevel option:selected').val();
        // console.log('skillName : ', skillName, ", skillLevel : ", skillLevel);

        skillList.push({
            'skillName': skillName,
            'skillLevel': skillLevel
        });

        $('#ulUserSkillList').append(
            '<tr>' +
                '<td class="pl-3">' +
                    '<span class="spanSkillName">' + skillName + '</span>' +
                '</td>' +
                '<td class="text-center">' +
                    '<span class="spanSkillLevel">' + skillLevel + '</span>' +
                '</td>' +
                '<td class="text-center">' +
                    '<button class="btnSkillDelete btn btn-light btn-sm" type="button">삭제</button>' +
                '</td>' +
            '</tr>'
        );

        // console.log('Add Skill = ', skillList);
    });

    $(document).on('click', '.btnSkillDelete', function () {
        var parent = $(this).parent();
        var skillName = parent.parent().find('.spanSkillName').text();
        // console.log(skillName);

        parent.parent().remove();

        for (var i = 0; i < skillList.length; i++) {
            if (skillList[i]['skillName'] === skillName) {
                skillList.splice(i, 1);
                break;
            }
        }
    });

    // 자격증
    var certList = $('#inputUserCertificationList').val();
    certList = JSON.parse(certList);
    // console.log(certList);
    for (var i = 0; i < certList.length; i++) {
        // console.log(certList[i]['certificationName']);

        $('#ulUserCertificationList').append(
            '<tr>' +
                '<td class="text-center">' +
                    '<span class="spanCertName">' + certList[i]['certificationName'] + '</span>' +
                '</td>' +
                '<td class="text-center">' +
                    '<span class="spanCertOrg">' + certList[i]['authOrganization'] + '</span>' +
                '</td>' +
                '<td class="text-center">' +
                    '<button class="btnCertDelete btn btn-light btn-sm" type="button">삭제</button>' +
                '</td>' +
            '</tr>'
        );
    }

    $('#btnCertAdd').click(function (e) {
        var certName = $('#inputCertName').val();
        var certGetDate = $('#inputCertGetDate').val();
        var certOrg = $('#inputCertOrganization').val();
        // console.log('certName : ', certName, ", certGetDate : ", certGetDate, ", certOrg : ", certOrg);

        certList.push({
            // 'acqDate': certGetDate,
            'certificationName': certName,
            'authOrganization': certOrg
        });

        $('#ulUserCertificationList').append(
            '<tr>' +
                '<td class="text-center">' +
                    '<span class="spanCertName">' + certName + '</span>' +
                '</td>' +
                '<td class="text-center">' +
                    '<span class="spanCertOrg">' + certOrg + '</span>' +
                '</td>' +
                '<td class="text-center">' +
                    '<button class="btnCertDelete btn btn-light btn-sm" type="button">삭제</button>' +
                '</td>' +
            '</tr>'
        );

        // console.log('Add Cert = ', certList);
    });

    $(document).on('click', '.btnCertDelete', function () {
        var parent = $(this).parent();
        var certName = parent.parent().find('.spanCertName').text();
        console.log(certName);

        parent.parent().remove();

        for (var i = 0; i < certList.length; i++) {
            if (certList[i]['certificationName'] === certName) {
                certList.splice(i, 1);
                break;
            }
        }
    });

    // 경력사항
    careerList = $('#inputUserCareerList').val();
    careerList = JSON.parse(careerList);
    // console.log(careerList);
    for (var i = 0; i < careerList.length; i++) {
        // console.log(careerList[i]['certificationName']);
        $('#ulUserCareerList').append(
            // `<tr>
            //     <td class="text-center">
            //         <div id="spanCareerStartDate">` + careerList[i]['startDate'] + `</div>
            //         <div> ~ </div>
            //         <div id="spanCareerEndDate">` + careerList[i]['endDate'] + `</div>
            //     </td>
            //     <td class="text-center">
            //         <span id="spanCareerProjectName">` + careerList[i]['projectName'] + `</span>
            //     </td>
            //     <td class="text-center">
            //         <span id="spanCareerJobPosition">` + careerList[i]['jobPosition'] + `</span>
            //     </td>
            //     <td class="text-center">
            //         <span id="spanCareerJobDescription">` + careerList[i]['jobDescription'] + `</span>
            //     </td>
            //     <td class="text-center">
            //         <button class="btnCareerDelete btn btn-light btn-sm" type="button">삭제</button>
            //     </td>
            // </tr>`
            '<tr data-id="' + (careerBtnId) +'">' +
                '<th class="text-center" style="width:10%;min-width:90px;font-weight: 700;font-size:14px;border-top:1px solid #9c9c9c;">경력기간</th>' +
                '<td class="pl-3" style="width:80%;border-top:1px solid #9c9c9c;">' +
                    '<span id="spanCareerStartDate">' + careerList[i]['startDate'] + '</span>'+
                    '<span> ~ </span>' +
                    '<span id="spanCareerEndDate">' + careerList[i]['endDate'] + '</span>' +
                '</td>' +
                '<td class="text-center" rowspan="3" style="width:10%;border-bottom:1px solid #9c9c9c;vertical-align:middle;border-top:1px solid #9c9c9c;">' +
                    '<button class="btnCareerDelete btn btn-light btn-sm" data-id="' + (careerBtnId) +'" type="button">삭제</button>' +
                '</td>' +
            '</tr>' +
            '<tr data-id="' + (careerBtnId) +'">' +
                '<th class="text-center" style="font-weight: 700;font-size:14px;">프로젝트명</th>' +
                '<td class="pl-3">' +
                    '<span class="spanCareerProjectName">' + careerList[i]['projectName'] + '</span>' +
                '</td>' +
            '</tr>' +
            '<tr data-id="' + (careerBtnId) +'">' +
                '<th class="text-center" style="font-weight: 700;font-size:14px;border-bottom:1px solid #9c9c9c">주요업무</th>' +
                '<td class="pl-3" style="border-bottom:1px solid #9c9c9c">' +
                    '<span id="spanCareerJobDescription">' + careerList[i]['jobDescription'] + '</span>' +
                '</td>' +
            '</tr>'
        );
        careerBtnId++;
    }

    $('#btnCareerAdd').click(function (e) {
        var startDate = $('#inputCareerStartDate').val();
        var endDate = $('#inputCareerEndDate').val();
        var projectName = $('#inputCareerProjectName').val();
        var jobPosition = $('#inputCareerJobPosition').val();
        var jobDescription = $('#inputCareerJobDesc').val();
        console.log('projectName : ', projectName, ", startDate : ", startDate, ", endDate : ", endDate, ", jobPosition : ", jobPosition, ", jobDescription : ", jobDescription);
        careerList.push({
            'startDate': startDate,
            'endDate': endDate,
            'projectName': projectName,
            'jobPosition': jobPosition,
            'jobDescription': jobDescription,
        });

        $('#ulUserCareerList').append(
            // `<tr>
            //     <td class="text-center">
            //         <div id="spanCareerStartDate">` + startDate + `</div>
            //         <div> ~ </div>
            //         <div id="spanCareerEndDate">` + endDate + `</div>
            //     </td>
            //     <td class="text-center">
            //         <span id="spanCareerProjectName">` + projectName + `</span>
            //     </td>
            //     <td class="text-center">
            //         <span id="spanCareerJobPosition">` + jobPosition + `</span>
            //     </td>
            //     <td class="text-center">
            //         <span id="spanCareerJobDescription">` + jobDescription + `</span>
            //     </td>
            //     <td class="text-center">
            //         <button class="btnCareerDelete btn btn-light btn-sm" type="button">삭제</button>
            //     </td>
            // </tr>`
            '<tr data-id="' + careerBtnId + '">' +
                '<th class="text-center" style="width:100px;font-weight: 700;font-size:14px;border-top:1px solid #9c9c9c;">경력기간</th>' +
                '<td class="pl-3" style="border-top:1px solid #9c9c9c;">' +
                    '<span id="spanCareerStartDate">' + startDate + '</span>' +
                    '<span> ~ </span>' +
                    '<span id="spanCareerEndDate">' + endDate + '</span>' +
                '</td>' +
                '<td class="text-center" rowspan="3" style="border-bottom:1px solid #9c9c9c;vertical-align:middle;border-top:1px solid #9c9c9c;">' +
                    '<button class="btnCareerDelete btn btn-light btn-sm" data-id="' + careerBtnId + '" type="button">삭제</button>' +
                '</td>' +
            '</tr>' +
            '<tr data-id="' + careerBtnId + '">' +
                '<th class="text-center" style="font-weight: 700;font-size:14px;">프로젝트명</th>' +
                '<td class="pl-3">' +
                    '<span class="spanCareerProjectName">' + projectName + '</span>' +
                '</td>' +
            '</tr>' +
            '<tr data-id="' + careerBtnId + '">'  +
                '<th class="text-center" style="font-weight: 700;font-size:14px;border-bottom:1px solid #9c9c9c">주요업무</th>' +
                '<td class="pl-3" style="border-bottom:1px solid #9c9c9c">' +
                    '<span id="spanCareerJobDescription">' + jobDescription + '</span>' +
                '</td>' +
            '</tr>'
        );
        careerBtnId++;

        // console.log('Add Career = ', careerList);
    });

    $(document).on('click', '.btnCareerDelete', function () {
        var id = $(this).data('id');
        var parent = $(this).parent();
        var projectName = $(this).closest('tr').siblings('[data-id=' + id +']').find('.spanCareerProjectName').text();
        // console.log(projectName);


        $(this).closest('tbody').find('tr[data-id=' + id + ']').remove();

        for (var i = 0; i < careerList.length; i++) {
            if (careerList[i]['projectName'] === projectName) {
                careerList.splice(i, 1);
                break;
            }
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

    $('#submit').click(function (e) {

        if (!confirm('저장하시겠습니까?')) {
            e.preventDefault();
            return false;
        }
        // 보유 기술
        var jsonSkillList = JSON.stringify(skillList);
        // console.log(jsonSkillList);
        $('#inputUserSkillList').val(jsonSkillList);

        // 자격증
        var jsonCertList = JSON.stringify(certList);
        // console.log(jsonCertList);
        $('#inputUserCertificationList').val(jsonCertList);

        // 경력사항
        var jsonCareerList = JSON.stringify(careerList);
        // console.log(jsonCareerList);
        $('#inputUserCareerList').val(jsonCareerList);

        var $inputContainer = $('.selected-keywords-container');
        // $inputContainer.empty();

        if (!nicknameAvailableMap[$('input[name=nickname]').val()]) {
            alert('닉네임 중복체크를 진행해 주세요');
            return;
        }

        if (!cellphoneAvailableMap[$('#w5-two input[name=cellphone]').val()]) {
            alert('휴대전화번호 인증을 진행해 주세요');
            return;
        }

        if (selectedKeywords.length === 0 && $('input[name=selectedKeywordJson\\[\\]]').length === 0) {
            alert('나의 섹터와 관련 키워드를 등록해주세요(필수)');
            return;
        }

        if (selectedKeywords.length < 3) {
            alert('키워드를 3개 이상 선택 또는 입력해주세요. 많은 키워드를 선택할 수록 매칭 확률이 높아집니다. :)');
            return;
        }

        if (!$('select[name=careerYear] option:selected').val()) {
            alert('관련 업무경력을 선택해 주세요');
            return;
        }

        if (!$('select[name=userJobPreference\\.projectDuration] option:selected').val()) {
            alert('선호하는 프로젝트 기간을 입력해 주세요.');
            return;
        }

        if (!$('select[name=userJobPreference\\.projectSize] option:selected').val()) {
            alert('선호하는 프로젝트 규모를 입력해 주세요.');
            return;
        }

        // if (!$('select[name=userJobPreference\\.payCriteria] option:selected').val()) {
        //     alert('선호하는 급여수령 기준을 입력해 주세요.');
        //     return;
        // }

        // if (!$('select[name=userJobPreference\\.workPlace] option:selected').val()) {
        //     alert('선호하는 프로젝트 장소를 입력해 주세요.');
        //     return;
        // }

        $inputContainer.empty();
        for (var i = 0; i < selectedKeywords.length; i++) {
            $inputContainer.append('<input type="hidden" name="selectedKeywordJsonId[]" value="' + JSON.parse(selectedKeywords[i]).id + '"/>');
            $inputContainer.append('<input type="hidden" name="selectedKeywordJsonCategoryName[]" value="' + JSON.parse(selectedKeywords[i]).categoryName + '"/>');
            $inputContainer.append('<input type="hidden" name="selectedKeywordJsonKeyword[]" value="' + JSON.parse(selectedKeywords[i]).keyword + '"/>');
        }

        $.ajax({
            type: 'POST',
            url: '/freelancer/profile/modify',
            data: new FormData($('#form')[0]),
            processData: false,
            cache: false,
            contentType: false,
            success: function (data) {
                if (afterRedirect) {
                    alert("입력이 완료되었습니다. 다시 용역대금을 청구하세요.");
                    location.href = afterRedirect;
                } else if (confirm('클라이언트는 프리랜서의 프로필보다 포트폴리오를 더 중요하게 검토합니다. 포트폴리오를 등록하시겠습니까?')) {
                    location.href = '/freelancer/pickMeUp/list';
                } else {
                    location.href = '/freelancer/profile/index';
                }
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
            }
        });
    });

    $('#inputCertGetDate').datepicker({autoclose: true,
        language: "ko"});
    $('#inputCareerStartDate').datepicker({autoclose: true,
        language: "ko"});
    $('#inputCareerEndDate').datepicker({autoclose: true,
        language: "ko"});

    $('#inputCertGetDate').datepicker('setDate', 'today');
    $('#inputCareerStartDate').datepicker('setDate', 'today');
    $('#inputCareerEndDate').datepicker('setDate', 'today');
});

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
            // $('#btn-check-nickname').css('visibility', 'hidden');
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

function init() {
    var selectedBusinessType = $('select[name=businessType] option:selected').val();
    if ('INDIVIDUAL' === selectedBusinessType) {
        $('.indiv-info-container').show();
        $('.company-info-container').hide();
        $('input[name=taxType][value=COLLECTION_IN_ADVANCE]').attr('checked', 'checked');
    } else if ('CORP_BUSINESS' === selectedBusinessType) {
        $('.indiv-info-container').hide();
        $('.company-info-container').show();
        $('input[name=taxType][value=VAT]').attr('checked', 'checked');
    } else {
        $('.indiv-info-container').show();
        $('.company-info-container').show();
    }

    if (profileImageUrl) {
        $('.slim-result img.in').attr('src', profileImageUrl);
        $('.slim-result img.in').css('opacity', 1);
    }

    // $('#btn-check-nickname').css('visibility', 'hidden');

    getKeywords();
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

function validateStep(stepIndex) {
    if (stepIndex === 1) {
        if (!$('input[name=profileImageUrl]').val() && !$('input[name=uploadedProfileImageUrl]').val() ) {
            alert('이미지를 첨부하시거나 샘플이미지를 선택해주세요.');
            return false;
        }
    } else if (stepIndex === 2) {
        if (!$('input[name=name]').val() || !$('input[name=nickname]').val()
            || !$('input[name=email]').val()) {
            alert('필수 값을 입력 혹은 선택해 주세요.');
            return false;
        }
        if (!nicknameAvailableMap[$('input[name=nickname]').val()]) {
            alert('닉네임 중복체크를 진행해 주세요');
            return;
        }
        if ('true' !== $('input[name=cellphoneCertified]').val()) {
            alert('휴대전화번호 인증이 필요합니다.');
            return false;
        }
    }

    return true;
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