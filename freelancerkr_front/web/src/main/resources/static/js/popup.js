// 샘플 데이터
var profileSrc = ["https://dummyimage.com/300x200/E0E0E0/252525&text=profileImage1","https://dummyimage.com/300x200/E0E0E0/252525&text=profileImage2",
    "https://dummyimage.com/300x200/E0E0E0/252525&text=profileImage3","https://dummyimage.com/300x200/E0E0E0/252525&text=profileImage4"];
var profileName = ["프리랜서코리아","프리랜서코리아","프리랜서코리아","프리랜서코리아"];
var addedInfo = ["Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolorum, hic.","Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolorum, hic.",
    "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolorum, hic.",
    "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolorum, hic."];

// 커스터마이징한 이동버튼
var arrowLeft = "<button class='mfp-arrow arrow-left mfp-prevent-close'><i class='fas fa-angle-left fa-3x mfp-prevent-close'></i></button>";
var arrowRight = "<button class='mfp-arrow arrow-right mfp-prevent-close'><i class='fas fa-angle-right fa-3x mfp-prevent-close'></i></button>";

function openModal(index, flag, projectId){

    var selector = "";
    var selectorModal = "";
    if(flag == 'priority')
        selector= '.priority .card', selectorModal= '.priority .card-btn>a:first-child';
    else
        selector = '.sponsored .card', selectorModal = '.sponsored .card-btn>a:first-child';

    var total = $(selectorModal).length;

    var arrTemplate = [];

    [].forEach.call($(selector), function(elem,index) {
        var backgroundImageUrl = $(elem).find('.card-img div.card-img-top').css("background-image");
        backgroundImageUrl = backgroundImageUrl.replace('url(','').replace(')','').replace(/\"/gi, "");
        var title = $(elem).find('.card-title').text();
        var date = $(elem).find('.text-muted').text();
        var exposeName = $(elem).find('input[name=exposeName]').val();
        var userProfileImageUrl = $(elem).find('input[name=userProfileImageUrl]').val();
        var userEmail = $(elem).find('input[name=userEmail]').val();
        var userCellphone = $(elem).find('input[name=userCellphone]').val();
        var description = $(elem).find('input[name=description]').val();

        var template = '<div class="row pick_modal">';
        template += '<div class="col-md-8 row ml-0 mr-0">';
        // template += '<div class="mfp-container mfp-s-ready mfp-inline-holder">'
        // template += '<button title="Previous (Left arrow key)" type="button" class="mfp-arrow mfp-arrow-left mfp-prevent-close"></button>';
        // template += '<button title="Next (Right arrow key)" type="button" class="mfp-arrow mfp-arrow-right mfp-prevent-close"></button>';
        template += '<div class="col-md-12 image_container"  style="background-image: url(' + backgroundImageUrl + '); background-repeat: no-repeat; background-size: cover; background-position: center center;">';
        // template += '<img src="' + src + '" alt="" class="img-fluid">';
        template += '</div>';
        // template += '</div>';
        template += '</div>';
        template += '<div class="col-md-4 p-5">';
        template += '<div class="profile">';
        template += '<div class="image_container mr-3">';
        template += '<img src="'+ userProfileImageUrl +'" class="img-fluid"/>';
        template += '</div>';
        template += '<span class="name">'+ exposeName +'</span>';
        template += '<hr>'
        template += '</div>';
        template += '<div class="clearfix">';
        template += '<div class="title mb-1">'+ title +'</div>';
        template += '<div class="mb-5">'+ description +'</div>';
        template += '<div class="date">게시: ';
        template +=  date;
        template += '</div>';
        template +=  '<hr>'
        template += '<div class="sns_info">';
        template += '<div class="email">';
        template += '<span class="mr-2"><img src="/static/images/pp_icon_mail.png" alt="" /></span><span class="mr-2">이메일</span><span>' + userEmail + '</span>';
        template += '</div>';
        template += '<div class="phone">';
        template += '<span class="mr-2"><img src="/static/images/pp_icon_call.png" alt="" /></span><span class="mr-2">휴대폰</span><span>' + userCellphone + '</span>';
        template += '</div>';
        // template += '<div class="sns">';
        // template += '<span class="mr-2"><img src="/static/images/pp_icon_mail.png" alt="" /></span><span class="mr-2">SNS</span><span>인스타 freelancer.kr</span>';
        // template += '</div>'
        template += '</div>';
        template += '<hr>';
        template += '<div class="btn_container pb-5">';
        template += '<button type="button" class="large suggest" onclick="openProposeModal(' + projectId +' )">입찰 참여 제안하기</button>';
        template += '</div>';
        template += '<div class="social">';
        template += '<span><img src="/static/images/pp_icon_call.png" alt="" /></span><span><img src="/static/images/pp_icon_call.png" alt="" /></span>'
            +'<span><img src="/static/images/pp_icon_call.png" alt="" /></span>';
        template += '</div>';
        template += '</div>';
        template += '</div>';

        console.log($(template));
        console.log($(template)[0]);

        arrTemplate[index] = {src:$(template)[0], type:'inline'};

        // console.log(template);
        console.log(arrTemplate);
    });

    $(selectorModal).magnificPopup({
        items: arrTemplate,
        type: 'image',
        gallery: {
            enabled:true,
            // arrowLeft : arrowLeft,
            // arrowRight: arrowRight,
            tPrev: 'Previous (Left arrow key)', // title for left button
            tNext: 'Next (Right arrow key)', // title for right button
            tCounter: '1 of 5' // markup of counter
        },
        arrowLeft : arrowLeft,
        arrowRight: arrowRight
    }).magnificPopup('open');

    $(selectorModal).magnificPopup({
        items: arrTemplate,
        type: 'image',
        gallery: {
            enabled:true,
            // arrowLeft : arrowLeft,
            // arrowRight: arrowRight,
            tPrev: 'Previous (Left arrow key)', // title for left button
            tNext: 'Next (Right arrow key)', // title for right button
            tCounter: '1 of 5' // markup of counter
        },
        arrowLeft : arrowLeft,
        arrowRight: arrowRight
    }).magnificPopup('goTo', index);
}
