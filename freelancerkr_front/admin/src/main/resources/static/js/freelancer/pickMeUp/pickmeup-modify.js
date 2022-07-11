$(document).ready(function () {

    $('#modifyBtn').click(function (e) {

        if (!confirm('정말 수정하시겠습니까?')) {
            e.preventDefault();
            return false;
        }

        setContactAvailableDayTime();

        var category2 = $('input[name=category2nd]:checked').val();
        var title = $('input[name=title]').val();
        var workStartAt = $('input[name=workStartAt]').val();
        var workEndAt = $('input[name=workEndAt]').val();
        var description = $('textarea[name=description]').val();
        var uploadedMainImageUrl = $('input[name=uploadedMainImageUrl]').val();
        var payTypeAgreement = $("input[name=payTypeAgreement]:checked").val();
        var markupContent = $('#summernote').summernote('code');
        var workPlace = $('select[name=workPlace] option:selected').val();
        var payType = $('select[name=payType] option:selected').val();

        $("input[name=payTypeNeedAgreement]").val(JSON.parse(payTypeAgreement));


        if (!category2) {
            e.preventDefault();
            alert('프로필에서 정하신 섹터 중 하나를 선택해주세요.');
            return false;
        }

        if (!title) {
            e.preventDefault();
            alert('제목을 입력해 주세요');
            return false;
        }

        if (contentType !== 'BLOG' && !description) {
            e.preventDefault();
            alert('포트폴리오에 대한 소개를 입력해주세요.');
            return false;
        }

        if (contentType === 'BLOG' && !$(markupContent).text()) {
            e.preventDefault();
            alert('포트폴리오에 내용을 입력해주세요.');
            return false;
        }

        if (contentType !== 'BLOG' && description.length > 200) {
            e.preventDefault();
            alert('포트폴리오내용은 200자를 넘을 수 없습니다.');
            return false;
        }

        if (contentType !== 'BLOG' && !uploadedMainImageUrl) {
            e.preventDefault();
            alert('대표 이미지를 선택 후 업로드 해주세요.');
            return false;
        }

        if (!JSON.parse($("input[name=payTypeAgreement]:checked").val()) && !$('input[name=minimumPay]').val()) {
            e.preventDefault();
            alert('최소 희망 보수를 선택(입력)해주세요.');
            return;
        }

        if (!$('input[name=minimumPay]').val()) {
            $('input[name=minimumPay]').val(0);
        }

        if (!workPlace) {
            e.preventDefault();
            alert('선호하시는 작업위치를 선택해주세요.');
            return;
        }

        if (workPlace === 'OFF_LINE' && (!$('input[name=workPlaceAddress1]').val() || !$('input[name=workPlaceAddress2]').val())) {
            e.preventDefault();
            alert('선호하시는 작업위치를 선택해주세요.');
            return;
        }


        if ($('.template-upload').length > 0) {
            e.preventDefault();
            alert('업로드 완료 하지 않은 추가 이미지 혹은 동영상 내역이 있습니다. 업로드 완료 혹은 취소 해주세요.');
            return false;
        }

        if (!$('.mainPictureContainer input[name=mainImageUrl]').val()) {
            $('#form input[name=mainImageUrl]').val(uploadedMainImageUrl);
        } else {
            $('#form input[name=mainImageUrl]').val($('.mainPictureContainer input[name=mainImageUrl]').val())
        }
        $('input[name=subImageUrl\\[\\]]').remove();

        var elems = $('#fileupload input[name=subPiecesFileUrl\\[\\]]');

        for (var i = 0; i<elems.length; i++) {
            var elem = elems[i];
            $('#form').append(
                '<input type="hidden" name="subImageUrl[]" value="' + $(elem).val() + '" />'
            );
        }

        $('#form').submit();
    });
});
