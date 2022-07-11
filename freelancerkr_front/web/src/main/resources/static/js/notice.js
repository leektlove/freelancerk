// var loading = false;
// var currentPage = -1;
//
// $(document).ready(function () {
//     $('#contents').masonry({
//         // options
//         itemSelector: '.masonry-item',
//     });
//     loadMore();
// });
// $(window).scroll(function () {
//     if ($(window).scrollTop() + $(window).height() >= $(document).height() - 180) {
//         if (loading) return;
//         loadMore();
//     }
// });
//
// function loadMore() {
//     if (loading) return;
//     loading = true;
//     currentPage = currentPage + 1;
//     var url = '/notices?pageNumber=' + (parseInt(currentPage));
//
//     $.ajax({
//         type: 'GET',
//         url: url,
//         success: function (response) {
//             if ('SUCCESS' === response.responseCode) {
//                 var list = response.data;
//                 for (var i = 0; i < list.length; i++) {
//                     var item = list[i];
//                     if ('NOTICE' === item.type) {
//                         var el =
//                             '<div class="masonry-item no-default-style col-12 col-sm-6 col-md-4 col-lg-3" data-url="' + '/common/notice?noticeId=' + item.id + '">\n' +
//                             '<span class="thumb-info thumb-info-no-borders thumb-info-no-borders-rounded thumb-info-bottom-info thumb-info-bottom-info-dark thumb-info-bottom-info-dark-linear thumb-info-hide-info-hover">\n' +
//                             '<span class="thumb-info-wrapper mb-4">\n' +
//                             ' <img src="' + item.imageUrl + '" class="img-fluid" alt="">\n' +
//                             '<span class="thumb-info-title">\n' +
//                             '<span class="thumb-info-inner line-height-8">' + item.title + '</span>\n' +
//                             '<span class="thumb-info-type line-height-3">' + item.abbreviatedContents + '</span>\n' +
//                             '</span>\n' +
//                             '</span>\n' +
//                             '</span>\n' +
//                             '</div>';
//                         $("#contents").append(el).each(function () {
//                             $('#contents').masonry('reloadItems');
//                             // $(this).load(function() {
//                             // });
//                         });
//                         $("#contents").masonry({
//                             isAnimated: false,
//                             transitionDuration: 0,
//                         });
//                     } else if ('LALA' === item.type) {
//                         var el =
//                             '<div class="masonry-item no-default-style col-12 col-sm-6 col-md-4 col-lg-3" data-url="' + '/common/blog/' + item.id + '">\n' +
//                             '<span class="thumb-info thumb-info-no-borders thumb-info-no-borders-rounded thumb-info-bottom-info thumb-info-bottom-info-dark thumb-info-bottom-info-dark-linear thumb-info-hide-info-hover">\n' +
//                             '<span class="thumb-info-wrapper mb-4">\n' +
//                             ' <img src="' + item.mainImageUrl + '" class="img-fluid" alt="">\n' +
//                             '<span class="thumb-info-title">\n' +
//                             '<span class="thumb-info-inner line-height-8">' + item.title + '</span>\n' +
//                             '<span class="thumb-info-type line-height-3">' + item.subtitle + '</span>\n' +
//                             '</span>\n' +
//                             '</span>\n' +
//                             '</span>\n' +
//                             '</div>';
//
//
//                         $("#contents").append(el).each(function () {
//
//                             $('#contents').masonry('reloadItems');
//                             // $(this).load(function() {
//                             // });
//                         });
//                         $("#contents").masonry({
//                             isAnimated: false,
//                             transitionDuration: 0
//                         });
//                     }
//                 }
//                 $("#contents").masonry();
//                 setTimeout(function () {
//                     $("#contents").masonry({
//                         isAnimated: false,
//                         transitionDuration: 0
//                     });
//                 }, 2000);
//                 loading = false;
//             } else {
//
//             }
//         },
//         error: function (error) {
//             console.error(error);
//         }
//     });
// }
//
// $(document).on('click', '.masonry-item', function () {
//     var url = $(this).data('url');
//     location.href = url;
// })