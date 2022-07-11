$(document).ready(function () {
    console.log("pick-list.js");
    // 찜한 프로젝트 삭제하기
    $('.btnDeleteLike').on("click", function (e) {
        e.preventDefault();
        let projectId = $(this).parent().find('.projectId').val();

        var formData = new FormData();
        formData.append('projectId', projectId);

        $.ajax({
            method: 'POST',
            url: '/deleteFromWishList',
            data: formData,
            processData: false,
            cache: false,
            contentType: false,
            success: function(result) {
                alert("찜하기가 삭제되었습니다.");
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });
});