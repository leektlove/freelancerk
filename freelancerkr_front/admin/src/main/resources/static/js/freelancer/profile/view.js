function toRegisterPortfolio() {
    if (!userInfoInput) {
        alert('프로필을 먼저 입력하신 후 포트폴리오를 등록해주세요.');
        location.href='/freelancer/profile/modify';
        return;
    }
    // if (totalPortfolioCount >= 8) {
    //     alert('포트폴리오는 8개까지 등록할 수 있습니다');
    //     return;
    // }
    location.href='/freelancer/pickMeUp/choiceEnrollment';
}

function loadMoreMessage() {
    var url = '/users/messages?pageNumber=' + (parseInt(currentPage)+1);

    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            if ('SUCCESS' === response.responseCode) {
                currentPage++;
                var data = response.data;
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var elem = '<li>\n' +
                        '<div class="tm-box">\n' +
                        '<p class="text-muted mb-0">' + item.pastTime + '</p>\n' +
                        '<p>' + item.content + '</p>\n' +
                        '</div>\n' +
                        ' </li>';
                    $('#messagesContainer').append(elem);
                }
                if (currentPage === response.totalPages) {
                    $('#messageLoadMoreBlock').hide();
                }
            }
        },
        error: function(error) {
            console.error(error);
        }
    });
}