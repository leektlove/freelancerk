var croppedImageSelector;
var videoPreviewSelector;
var hiddenInputSelector;
var croppedHiddenInputSelector;
var originInputSelector;

$(document).ready(function () {
    var $modal = $('#imageModal');
    var imageCandidate = document.getElementById('croppedImageCandidate');
    var cropper;

    $(".comment-view").scrollTop($(".comment-view").scrollHeight);

    $modal.on('shown.bs.modal', function () {
        cropper = new Cropper(imageCandidate, {
            aspectRatio: 1,
            viewMode: 3,
        });
    }).on('hidden.bs.modal', function () {
        cropper.destroy();
        cropper = null;
    });

    $('#form').submit(function(e) {
        if (!userInfoInput) {
            if (confirm('프로필 업로드 완료 후 가능합니다.')) {
                location.href='/freelancer/profile/modify';
                return;
            }
            return;
        }

        if (!confirm('본 컨테스트 취지와 상이한 작품 제출은 임의 삭제되며, 고의인 경우 업무방해죄에 해당합니다. 타인 작품 등의 지적재산권을 무단 도용한 경우 법적 처벌되며 상당한 금액의 배상책임이 있습니다. 계속 진행하시겠습니까?')) {
            e.preventDefault();
            return false;
        }

        if ($('input[name=mainPiecesFileUrl]').length === 0) {
            alert('메인 작품을 등록해 주세요');
            return;
        }

        return true;
    });

    $('input[name=portfolioMainPic]').on('change', function() {

        if (checkImageType(this.files[0]) && !checkImageSize(this.files[0])) {
            $(this).val('');
            alert('5MB 넘는 이미지는 업로드할 수 없습니다.');
            return;
        } else if (checkVideoType(this.files[0]) && !checkVideoSize(this.files[0])) {
            $(this).val('');
            alert('20MB 넘는 동영상은 업로드할 수 없습니다.');
            return;
        } else if (!checkImageType(this.files[0]) && !checkVideoType(this.files[0])) {
            $(this).val('');
            alert('이미지 혹은 동영상 파일이 아닙니다.');
            return;
        }

        originInputSelector = $('input[name=portfolioMainPic]');
        hiddenInputSelector = $('.mainPictureContainer input[name=mainPiecesFileUrl]');
        croppedHiddenInputSelector = $('.mainPictureContainer input[name=croppedMainPiecesFileUrl]');

        if (checkImageType(this.files[0])) {
            var done = function (url) {
                imageCandidate.src = url;
                $modal.modal('show');
            };
            croppedImageSelector = $('.mainPictureContainer img');

            readURL($(this)[0], null, done);
            $('.mainVideoContainer').hide();
        } else if (checkVideoType(this.files[0])) {
            var formData = new FormData();

            formData.append('file', this.files[0]);
            $.ajax('/files', {
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (res) {
                    $(hiddenInputSelector).val(res);
                    $('.mainVideoContainer').show();
                    $('video#mainVideoCandidate').attr('src', res);
                    $('#videoThumbnailContainer').append('<img id="videoLoadingGif" src="/static/images/loading.gif" style="width: 50px; height: 50px;"/>');

                    var formData = new FormData();
                    formData.append('fileUrl', res);

                    $.ajax('/files/thumbnails', {
                        method: 'POST',
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: function(res) {
                            $('#videoThumbnailContainer').empty();
                            var thumbnailUrls = res.data;
                            console.log(thumbnailUrls);
                            for (var i = 0; i<thumbnailUrls.length; i++) {
                                $('#videoThumbnailContainer').append('<div style=""><img style="max-width: 400px; margin-right: 15px;" src="' + thumbnailUrls[i] + '"/><div style="text-align: center;"><input type="radio" name="videoThumbnailImageUrl" value="' + thumbnailUrls[i] + '"/></div></div>');
                            }
                        },
                        error: function() {
                            $('#videoThumbnailContainer').empty();
                            alert('비디오 썸네일 생성에 실패했습니다. 시스템 관리자에 문의해 주세요.');
                        }
                    });

                },

                error: function () {
                    alert('파일 저장에 실패했습니다.');
                },

                complete: function () {

                },
            });
        }
    });

    $('body').on('change', 'input[name=pickMeUpSubPics]', function() {

        if (checkImageType(this.files[0]) && !checkImageSize(this.files[0])) {
            $(this).val('');
            alert('5MB 넘는 이미지는 업로드할 수 없습니다.');
            return;
        } else if (checkVideoType(this.files[0]) && !checkVideoSize(this.files[0])) {
            $(this).val('');
            alert('20MB 넘는 동영상은 업로드할 수 없습니다.');
            return;
        } else if (!checkImageType(this.files[0]) && !checkVideoType(this.files[0])) {
            $(this).val('');
            alert('이미지 혹은 동영상 파일이 아닙니다.');
            return;
        }

        originInputSelector = $(this);
        hiddenInputSelector = $(this).siblings('input[name=subPiecesFileUrl\\[\\]]');

        var imageSelector = $(this).siblings('div').find('img')[0];

        if (checkImageType(this.files[0])) {

            var formDataForOriginal = new FormData();
            formDataForOriginal.append('file', originInputSelector[0].files[0]);

            $.ajax('/files', {
                method: 'POST',
                data: formDataForOriginal,
                processData: false,
                contentType: false,
                success: function (res) {
                    $(hiddenInputSelector).val(res);
                    $(imageSelector).attr('src', res);
                },

                error: function () {
                    alert('이미지 저장에 실패했습니다.');
                },

                complete: function () {
                },
            });

        } else if (checkVideoType(this.files[0])) {
            var videoSelector = $(this).siblings('div').find('video')[0];
            $(videoSelector).show();
            var formData = new FormData();
            formData.append('file', this.files[0]);
            $.ajax('/files', {
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (res) {
                    $(hiddenInputSelector).val(res);
                    $(videoSelector).attr('src', res);
                },

                error: function () {
                    alert('파일 저장에 실패했습니다.');
                },

                complete: function () {

                },
            });
        }

        $(this).hide();
    });

    // 포트폴리오 상세 이미지 추가
    var subPicCount = 0;
    $('#btnAddInputFile').click(function (e) {
        if (subPicCount >= 2) {
            alert('상세 파일은 2장이상 추가할 수 없습니다.');
            return;
        }
        $('#tdDetailPicSpace').append(
            `<div>
                <input name="pickMeUpSubPics" type="file" class="form-control-file">
                <input name="subPiecesFileUrl[]" type="hidden">
                <input name="croppedSubPiecesFileUrl[]" type="hidden">
                <div class="">
                    <img src="" style="max-width: 80%"/>
                    <video src="" style="max-width: 80%; display: none" controls/>
                </div>
            </div>`
        );
        subPicCount++;
    });

    // 포트폴리오 상세 이미지 제거
    $('#btnDeleteInputFile').click(function (e) {
        if (subPicCount > 0) {
            $('#tdDetailPicSpace :last-child').remove();
            subPicCount--;
        }
    });

    $('#registerBtn').click(function (e) {
        if(!$('input[name=mainPiecesFileUrl]').val()) {
            alert('먼저 제출하실 작품을 등록해주세요. 총 3개까지 등록 가능합니다.');
            return;
        }

        if ($('.template-upload').length > 0) {
            e.preventDefault();
            alert('업로드 완료 하지 않은 추가 이미지 혹은 동영상 내역이 있습니다. 업로드 완료 혹은 취소 해주세요.');
            return false;
        }

        var elems = $('#fileupload input[name=subPiecesFileUrl\\[\\]]');

        for (var i = 0; i<elems.length; i++) {
            var elem = elems[i];
            $('#form').append(
                '<input type="hidden" name="subPiecesFileUrl[]" value="' + $(elem).val() + '" />'
            );
        }
        $('#form').submit();

        return "freelancer/details/contestApplyDone";
    });

    $('div.mainPictureContainer').click(function(e) {
        e.stopPropagation();
        if (useNdaIp && !$('input#checkNdaIp').is(':checked')) {
            $('#nda-ip-option-info').modal('show');
            return false;
        } else {
            $('div.mainPictureContainer > .slim').trigger('click');
            return false;
        }
    });
});

function completeModify() {
    if (!confirm('수정하시겠습니까?')) {
        return
    }

    if ($('.template-upload').length > 0) {
        alert('업로드 완료 하지 않은 추가 이미지 혹은 동영상 내역이 있습니다. 업로드 완료 혹은 취소 해주세요.');
        return false;
    }

    if (!$('input[name=mainPiecesFileUrl]').val()) {
        var mainImageUrl = $('input[name=uploadedMainImageUrl]').val();
        $('input[name=mainPiecesFileUrl]').val(mainImageUrl);
    }

    $.ajax({
        type: 'POST',
        url: $('#contestEntryFileForm').attr('action'),
        data: $('#contestEntryFileForm').serialize() + '&' + $('#fileupload').serialize(),
        success: function (data) {
            location.href = '/freelancer/contestDetail/payDone';
        },
        error: function (error) {
            alert('서버와의 통신 중 문제가 발생하였습니다.');
        }
    });
}


