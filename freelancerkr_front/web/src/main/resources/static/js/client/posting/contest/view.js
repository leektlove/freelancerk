$(document).ready(function() {

    $('input[name=referenceFile]').on('change', function() {
        if (!checkImageSize(this.files[0])) {
            alert('파일용량은 5M로 제한됩니다. 다시 업로드해주세요.');
            $('input[name=referenceFile]').val('');
            return false;
        }
        $('#attachedFileCancelUpload').show();
        readURLAsBg($(this)[0], $('#preview_pop #preview-box .project_ca_img'));
    });

    $('input[name=referenceFile]').on('click', function() {
        $(this).val('');
    });

    $('#attachedFileCancelUpload').click(function() {
        $('input[name=referenceFile]').val('');
        $(this).hide();
    });

    $('#uploadedFileCancelUpload').click(function() {
        $('#descriptionFileUploadedContainer').hide();
        $('#descriptionFileUploadContainer').show();
    });

    $('input[name=contestSectorMetaTypeId\\[\\]]').on('click', function() {
        var $box = $(this);
        var selectedId = $(this).data('id');

        if ($box.is(":checked")) {
            // the name of the box is retrieved using the .attr() method
            // as it is assumed and expected to be immutable
            var group = "input:checkbox[name='" + $box.attr("name") + "']";
            // the checked state of the group/box on the other hand will change
            // and the current value is retrieved using .prop() method
            $(group).prop("checked", false);
            $('.collapse').removeClass('show');
            $box.prop("checked", true);

            let typeContainers = $(this).closest('.sector-container').find('.sector-type-container');

            for (let typeContainer in typeContainers) {
                let sectorId = $(typeContainer).data('sector-id');
                if (selectedId === sectorId) continue;

                $(typeContainer).find('.item_detail input').prop('checked', false);
            }

        } else {
            $box.prop("checked", false);
        }
    });

    //체크박스 개수제한
    $("input[name=feeling]:checkbox").change(function() {// 체크박스들이 변경됬을때
        var n = '3';
        var cnt = n;
        if( cnt==$("input[name=feeling]:checkbox:checked").length ) {
            $(":checkbox:not(:checked)").attr("disabled", "disabled");
        } else {
            $("input[name=feeling]:checkbox").removeAttr("disabled");
        }
    });

    $('input:checkbox[name=contestSectorMetaTypeId\\[\\]]').change(function() {
        if ($(this).data('id') === 1 && $(this).is(':checked')) {
            $('#quantity-container').hide();
        } else {
            $('#quantity-container').show();
        }
    });

    $('#saveTemporarily').click(function() {

        var sectionMetaIds = [];
        $("input[name=contestSectorMetaTypeId\\[\\]]:checked").each(function () {
            sectionMetaIds.push($(this).val());
        });

        var sectionIds = [];
        $("input[name=contestSectorTypeId\\[\\]]:checked").each(function () {
            sectionIds.push($(this).val());
        });

        if (!sectionIds.length && !modifyMode) {
            alert('섹터를 선택해주세요');
            return;
        }

        if (!$('input[name=title]').val()) {
            alert('이름을 입력해주세요');
            $('input[name=title]').focus();
            return;
        }
        if (!$('input[name=categoryOfBusiness]').val()) {
            alert('업종을 입력해주세요.');
            $('input[name=categoryOfBusiness]').focus();
            return;
        }
        if (!$('input[name=majorProduct]').val()) {
            alert('주요제품을 입력해주세요.');
            $('input[name=majorProduct]').focus();
            return false;
        }
        var description = $('textarea[name=description]').val();
        description = description.replace(/&nbsp;/gi,'').replace(/\s/g, "");
        if (!$(description).text()) {
            alert('자세한 설명을 입력해주세요.');
            $('textarea[name=description]').focus();
            return;
        }
        if (!isInArray(1, sectionMetaIds) && !$('textarea[name=descriptionPerPage]').val()) {
            alert('분량에 관한 설명을 입력해주세요.');
            $('textarea[name=descriptionPerPage]').focus();
            return;
        }
        if (!$('input[name=priorityTone]').val()) {
            alert('선호 색상을 알려주세요');
            $('input[name=priorityTone]').focus();
            return;
        }

        var feelingIds = [];
        $("input[name=feeling]:checked").each(function () {
            feelingIds.push($(this).val());
        });

        if (!feelingIds.length) {
            alert('선호 느낌을 선택해주세요');
            return false;
        }

        $.ajax({
            url: '/contest/temp',
            method: 'POST',
            data: new FormData($('#contestForm')[0]),
            processData: false,
            cache: false,
            contentType: false,
            success: function(response) {
                if (modifyMode) {
                    alert('포스팅 옵션에 대한 추가/변경 등은 [결제관리]에서 가능합니다. 단 컨테스트 섹터와 상금을 수정하시려면 본 컨테스트를 취소하시고 다시 포스팅 하십시오. ');
                    location.href = '/view/pick-me-ups?afterPosting=true&category1stId=91';
                } else {
                    location.href = '/client/posting/contestOption?contestId=' + response.data;
                }
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    $('#cancel').click(function() {
        if(!confirm('취소 하시겠습니끼?')) {
            return;
        }

        history.back();
    })

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
            ['color',['color']],
            ['para', ['paragraph']],
            ['height',['height']],
            ['insert', ['link', 'picture', 'video']],
            ['table',['table']],
            ['hr',['hr']],
            ['view', ['undo','redo']],
        ]
    });
});

function validateStep(stepIndex) {
    if (stepIndex === 1) {
        if (!$('input[name=title]').val()) {
            alert('이름을 입력해주세요');
            $('input[name=title]').focus();
            return false;
        }

        if (!$('input[name=title]').val().length > 255) {
            alert('컨테스트 명은 255자를 초과할 수 없습니다.');
            return false;
            $('input[name=title]').focus();
            return false;
        }

        var sectionIds = [];
        $("input[name=contestSectorTypeId\\[\\]]:checked").each(function () {
            sectionIds.push($(this).val());
        });

        if (!sectionIds.length && !modifyMode) {
            alert('섹터를 선택해주세요');
            return false;
        }

    } else if (stepIndex === 2) {
        if (!$('input[name=categoryOfBusiness]').val()) {
            alert('업종을 입력해주세요.');
            $('input[name=categoryOfBusiness]').focus();
            return false;
        }
        if (!$('input[name=majorProduct]').val()) {
            alert('주요제품을 입력해주세요.');
            $('input[name=majorProduct]').focus();
            return false;
        }
        var description = $('textarea[name=description]').val();
        description = description.replace(/&nbsp;/gi,'').replace(/\s/g, "");
        if (!$(description).text()) {
            alert('자세한 설명을 입력해주세요.');
            return false;
        }

    } else if (stepIndex === 3) {
        var sectionMetaIds = [];
        $("input[name=contestSectorMetaTypeId\\[\\]]:checked").each(function () {
            sectionMetaIds.push($(this).val());
        });

        if (!isInArray(1, sectionMetaIds) && !$('textarea[name=descriptionPerPage]').val()) {
            alert('분량에 관한 설명을 입력해주세요.');
            $('textarea[name=descriptionPerPage]').focus();
            return false;
        }
    } else if (stepIndex === 4) {

        if (!$('input[name=priorityTone]').val()) {
            alert('선호 색상 알려주세요');
            $('input[name=priorityTone]').focus();
            return false;
        }

        var feelingIds = [];
        $("input[name=feeling]:checked").each(function () {
            feelingIds.push($(this).val());
        });

        if (!feelingIds.length) {
            alert('선호 느낌을 선택해주세요');
            return false;
        }
    }

    return true;
}

//textbox 글자수제한
function countChar(val) {
    var content = $('textarea[name=description]').val();
    var len = content.length;
    if (len >= 1000) {
        $('textarea[name=description]').val(content.substring(0, 1000));
    } else {
        $('.textCnt').text(len.toLocaleString());
    }
}
function countChar2(val) {
    var content = $('textarea[name=descriptionPerPage]').val();
    var len = content.length;
    if (len >= 1000) {
        $('textarea[name=descriptionPerPage]').val(content.substring(0, 1000));
    } else {
        $('.textCnt2').text(len.toLocaleString());
    }
}
function countCorporateNameOrCatchPhrase(val) {
    var content = $('textarea[name=corporateNameOrCatchPhrase]').val();
    var len = content.length;
    if (len >= 1000) {
        $('textarea[name=corporateNameOrCatchPhrase]').val(content.substring(0, 1000));
    } else {
        $('.corporateNameOrCatchPhraseTextCnt').text(len.toLocaleString());
    }
}
function countCharPriorityTone(val) {
    var content = $('input[name=priorityTone]').val();
    var len = content.length;
    if (len >= 100) {
        $('input[name=priorityTone]').val(content.substring(0, 1000));
    } else {
        $('.priorityToneTextCnt').text(len.toLocaleString());
    }
}