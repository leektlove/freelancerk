var currentPage = 0;
var pageSize = 8;
var pageEnd = false;
var allSelectMode = false;

$(document).ready(function() {
    $('button#selectAllBtn').click(function() {
        if (allSelectMode) {
            var checkbox = $(".direct_market input[type=checkbox]");
            for(var i=0; i<checkbox.length; i++) {
                $(checkbox[i]).prop('checked', false);

            }
            allSelectMode = false;
        } else {
            var checkbox = $(".direct_market input[type=checkbox]");
            for(var i=0; i<checkbox.length; i++) {
                $(checkbox[i]).prop('checked', true);

            }
            allSelectMode = true;
        }
    });

    $('button#delete').click(function() {
        if (!confirm('선택하신 직거래 가능 포트폴리오를 삭제하시겠습니까? 메모도 함께 삭제됩니다.')) {
            return;
        }

        var checkedDirectDealIdArr = $(".direct_market input:checked").map(function () {
            return $(this).data('id')
        }).get();

        if (checkedDirectDealIdArr.length === 0) {
            alert('삭제할 항목을 선택해 주세요');
            return;
        }

        var query = "";
        for (var i = 0; i < checkedDirectDealIdArr.length; i++) {
            if (i === 0) {
                query += ('?id[]=' + checkedDirectDealIdArr[i]);
            } else {
                query += ('&id[]=' + checkedDirectDealIdArr[i]);
            }
        }

        $.ajax({
            type: 'DELETE',
            url: '/direct-deals' + encodeURI(query),
            success: function () {
                alert('성공적으로 삭제되었습니다.');
                location.reload();
            },
            error: function() {
                alert('요청에 실패했습니다. 다시 시도해 주시거나, 문제가 계속될 경우 고객센터로 문의 주세요.');
            }
        });
    });

    $('button#submit').click(function() {
        if (!confirm('선택한 전문가와 프로젝트를 진행하시겠습니까?')) {
            return;
        }
        var checkedDirectDealIdArr = $(".direct_market input:checked").map(function () {
            return $(this).data('id')
        }).get();

        if (checkedDirectDealIdArr.length === 0) {
            alert('먼저 전문가를 선택해주세요.');
            return;
        }

        if (checkedDirectDealIdArr.length > 1) {
            alert('프리랜서가 이중 선택 되었습니다. 한명씩 진행해주세요.');
            return;
        }

        location.href = '/client/directMarket/startWithSelectedSpecialist?directDealId=' + checkedDirectDealIdArr[0];
    });

    $('#show_pickmeup').click(function() {
        location.href='/view/pick-me-ups';
    });

    $('#select_all').click(function() {
        $('.card input[type=checkbox]').attr('checked', true);
    });
});

function saveComment(id) {

    var content = $('textarea[data-id=' + id + ']').val();
    if (!content) {
        alert('내용을 입력해 주세요');
    }

    var formData = new FormData();
    formData.append('content', content);

    $.ajax({
        type: 'POST',
        url: '/direct-deals/' + id + '/memo',
        data: formData,
        processData: false,
        cache: false,
        contentType: false,
        success: function() {
            alert('저장되었습니다.');
            location.reload();
        },
        error: function() {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    });
}

function loadMore() {
    if (pageEnd) {
        return;
    }

    currentPage++;

    $.ajax({
        url: '/users/me/direct-deals?pageNumber=' + currentPage + '&pageSize=' + pageSize,
        type: 'GET',
        success: function(response) {
            if (response.data.length === 0) {
                pageEnd = true;
                return;
            }


            for (var i = 0; i < response.data.length; i++) {
                var item = response.data[i];

                var itemElem = `<div class="col-lg-3">
							<div class="mb-4">
								<label class="mui-selectable-image">
									<input type="radio" name="si1" data-id="${item.id}">
									<b class="indicator"></b>
									<b class="indicator-back"></b>
									<span class="thumb-info thumb-info-no-borders thumb-info-no-borders-rounded thumb-info-centered-info thumb-info-no-zoom thumb-info-centered-icons thumb-info-slide-info-hover">
										<span class="thumb-info-wrapper">
											<div class="img_con" style="background-image:url('${item.pickMeUp.representativeImageUrl}'); ;background-size: cover; height: 255px;"></div>
											<span class="thumb-info-title">
												<span class="thumb-info-slide-info-hover-1">
													<span class="thumb-info-inner text-1">
														<div class="text-5">
															<span>${item.pickMeUp.user.exposeName}</span>
														</div>
														<div>
															<span></span>
											            	<span>${item.pickMeUp.user.email}</span>
											            </div>
											            <div>
											            	<span></span>
											            	<span>${item.pickMeUp.user.cellphone}</span>
											            </div>
													</span>
												</span>
												<span class="thumb-info-slide-info-hover-2">
													<span class="thumb-info-action">
														<div class="detail">
											            	<textarea class="form-control rounded-0" name="content" data-id="${item.id}"  placeholder="이곳에 메모를 해두세요(상대방에게는 보이지 않습니다.)" rows="5" >${item.pickMeUp.memo!=null?item.pickMeUp.memo:''}</textarea>
											            	<button class="btn btn-primary rounded-0 btn-block btn-lg" type="button" onclick="saveComment(${item.id})">저장</button>
											        	</div>
													</span>
												</span>
											</span>
										</span>
									</span>
								</label>
							</div>
						</div>`;
                console.log(item);

                $('.item-container').append(itemElem);
            }
        },
        error: function() {

        }
    })
}