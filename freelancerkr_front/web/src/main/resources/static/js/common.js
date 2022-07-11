function commaSplit(n) {// 콤마 나누는 부분
    var txtNumber = '' + n;
    var rxSplit = new RegExp('([0-9])([0-9][0-9][0-9][,.])');
    var arrNumber = txtNumber.split('.');
    arrNumber[0] += '.';
    do {
        arrNumber[0] = arrNumber[0].replace(rxSplit, '$1,$2');
    }
    while (rxSplit.test(arrNumber[0]));
    if(arrNumber.length > 1) {
        return arrNumber.join('');
    } else {
        return arrNumber[0].split('.')[0];
    }
}
 
function removeComma(n) {  // 콤마제거
    if ( typeof n == "undefined" || n == null || n == "" ) {
        return "";
    }
    var txtNumber = '' + n;
    return txtNumber.replace(/(,)/g, "");
}

function comma(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}

function numberWithCommas(n) {
    // console.log(n)
    if ( typeof n == "undefined" || n == null || n == "" ) {
        return "";
    }
    n.value = n.value.replace(/\D/, '');
    var rgx1 = /\D/g;  // /[^0-9]/g 와 같은 표현
    var parts=n.value.toString().split(".");
    parts[0] = parts[0].replace(rgx1,"");
    n.value =  parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + ((parts.length == 2)?'.'+parts[1]:'');
}

// 출력시 콤마
function addComma(num) {
  var regexp = /\B(?=(\d{3})+(?!\d))/g;
  return num.toString().replace(regexp, ',');
}

function passwordStrengthTxt(number) {
    if (0 === number || 1 === number) {
        return '<span style="color:black">나쁨</span>';
    } else if (2 === number || 3 === number) {
        return '<span style="color:red">보통</span>';
    } else {
        return '<span style="color:skyblue">좋음</span>'
    }
}

function openWindow(url) {
    if (url) {
        window.open(url);
    }
}

function isInArray(value, array) {
    return array.indexOf(value) > -1;
}

function getExtension(filename) {
    var parts = filename.split('.');
    return parts[parts.length - 1];
}

function isImage(filename) {
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
        case 'jpg':
        case 'gif':
        case 'bmp':
        case 'png':
            //etc
            return true;
    }
    return false;
}

function isVideo(filename) {
    var ext = getExtension(filename);
    switch (ext.toLowerCase()) {
        case 'm4v':
        case 'avi':
        case 'mpg':
        case 'mp4':
        case 'mov':
            // etc
            return true;
    }
    return false;
}

function setPaymentDataInModal(paymentData) {
    if (!paymentData) {
        alert('정보가 존재하지 않습니다.');
        $('#specModal').modal('hide');
        return;
    }
    $('#specModal .tax-type').hide();
    if ('VAT' === paymentData.taxType) {
        $('#specModal .tax-type.vat').show();
        $('#specModal .tax-type.collection-in-advance').hide();

        $('#specModal #vat').text(paymentData.vat.toLocaleString() + '원');
        $('#specModal .colspanvar').attr('colspan', '2');
    } else if ('COLLECTION_IN_ADVANCE' === paymentData.taxType) {
        $('#specModal .tax-type.vat').hide();
        $('#specModal .tax-type.collection-in-advance').show();

        $('#specModal #businessIncomeTax').text(paymentData.businessIncomeTax.toLocaleString() + '원');
        $('#specModal #localIncomeTax').text(paymentData.localIncomeTax.toLocaleString() + '원');
        $('#specModal .colspanvar').attr('colspan', '1');
    }
    $('#specModal #totalDeductedAmount').text(paymentData.totalDeductedAmount.toLocaleString() + '원');
    $('#specModal #freelancerName').text(paymentData.user.bankAccountName);
    $('#specModal #freelancerBankAccountInfo').text(paymentData.user.paymentInfo);
    $('#specModal #totalAmount').text(paymentData.amount.toLocaleString() + '원');
    $('#specModal #fee').text(paymentData.fee.toLocaleString() + '원');
    $('#specModal #realAmount').text(paymentData.realAmount.toLocaleString() + '원');
}

function checkVideoType(file) {
    var fileType = file['type'];
    var validImageTypes = ['video/mp4'];
    return validImageTypes.includes(fileType);
}

function checkImageType(file) {
    var fileType = file['type'];
    var validImageTypes = ['image/gif', 'image/jpeg', 'image/png'];
    return validImageTypes.includes(fileType);
}

function checkImageSize(file) {
    return file.size < 5000000;
}

function checkVideoSize(file) {
    return file.size < 20000000;
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function forIECenterCrop(){
    $('.center-crop-wrapper img').each(function(i, t) {
        var t = $(this), // 이미지 태그
            s = 'url(' + t.attr('src') + ')', // 이미지 태그의 src를 가져옴.
            p = t.parent(); // 부모 컨테이너 '.img-container'

        t.hide(); //이미지는 숨기고.

        p.css({
            'height'                : '100%',
            'background-size'       : 'cover',
            'background-repeat'     : 'no-repeat',
            'background-position'   : 'center',
            'background-image'      : s ,
        });
    });

}
//나이스스크롤 활성화
$(document).ready(function(){
    $(".scroller>").getNiceScroll().resize();
    $(".scroller").niceScroll();
});

$(document).ready(function() {
    $('#videoModal').on('shown.bs.modal', function(event) {
        var videoUrl = $(event.relatedTarget).data('url');
        $('#videoModal video').attr('src', videoUrl);
    });

// Internet Explorer 6-11
    var isIE = /*@cc_on!@*/false || !!document.documentMode;

    if (isIE) {
        forIECenterCrop();
    }

    // Opera 8.0+
    var isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;

// Firefox 1.0+
    var isFirefox = typeof InstallTrigger !== 'undefined';

// Safari 3.0+ "[object HTMLElementConstructor]"
    var isSafari = /constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || (typeof safari !== 'undefined' && safari.pushNotification));

// Internet Explorer 6-11
    var isIE = /*@cc_on!@*/false || !!document.documentMode;

// Edge 20+
    var isEdge = !isIE && !!window.StyleMedia;

// Chrome 1 - 71
    var isChrome = !!window.chrome && (!!window.chrome.webstore || !!window.chrome.runtime);

// Blink engine detection
    var isBlink = (isChrome || isOpera) && !!window.CSS;

    var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
    var isSamsungBrowser = navigator.userAgent.match(/SamsungBrowser/i)

    var md = new MobileDetect(window.navigator.userAgent);
    var agent = navigator.userAgent.toLowerCase();
    var isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);

    if (isIE) {
        $('.optimization_info').show();
        // alert('현재 인터넷 익스플로러 환경에서 프리랜서코리아를 사용 중입니다. 프리랜서코리아는 인터넷 익스프롤러 환경에 최적화되어있지 않기 때문에, 크롬 혹은 파이어폭스 사용을 권장 합니다.');
    } else if (!isSamsungBrowser && (isFirefox || isChrome || isSafari)) {
        // nothing;
        $('.optimization_info').hide();
    } else {
        var mobileAgent = md.userAgent();
        if (mobileAgent && mobileAgent !== 'Chrome') {
            $('.optimization_info').show();
            // alert('현재 크롬 혹은 파이어폭스 이외의 환경에서 프리랜서코리아를 사용 중입니다. 프리랜서코리아는 인터넷 크롬 혹은 파이어폭스 환경에 최적화되어 있기 때문에, 크롬 혹은 파이어폭스 사용을 권장 합니다.');
        } else if (isSamsungBrowser) {
            $('.optimization_info').show();
        }
    }
});


$(document).on('click', '#leaveBtn', function() {
    if (!confirm('탈퇴하시겠습니까?')) {
        return;
    }

    $.ajax({
        type: 'DELETE',
        url: '/users?leaveReason=' + encodeURIComponent($('select[name=leaveReason] option:selected').val()),
        success: function(response) {
            if ('SUCCESS' === response.responseCode) {
                alert('성공적으로 요청되었습니다.');
                location.href = '/logout';
            } else {
                alert('요청 중 문제가 발생했습니다.');
            }
        },
        error: function(error) {
            console.error(error);
            alert('요청 중 문제가 발생했습니다.');
        }
    })
});

String.prototype.byteLength = function() {
    var l= 0;

    for(var idx=0; idx < this.length; idx++) {
        var c = escape(this.charAt(idx));

        if( c.length==1 ) l ++;
        else if( c.indexOf("%u")!=-1 ) l += 2;
        else if( c.indexOf("%")!=-1 ) l += c.length/3;
    }

    return l;
};

// $(function() { 

//     $('.hamburguer-btn-sticky-dark').click(function() {
//         $("body").addClass("bg_no_scroll");
//         return false;
//     });

//     $('.hamburguer-btn-side-header').click(function() {
//         $("body").removeClass("bg_no_scroll");
//         return false;
//     });
// });

function onlyNumInput(){
    var code = window.event.keyCode;
    // console.log(code);
    if ((code >= 48 && code <= 57) || (code >= 96 && code <= 105) || code == 110 || code == 190 || code == 8 || code == 9 || code == 13  || code == 44){

        window.event.returnValue = true;
        return;
    }
    window.event.returnValue = false;
}

function viewKorean(num) {
    console.log(num)
    var hanA = new Array("","일","이","삼","사","오","육","칠","팔","구","십");
    var danA = new Array("","십","백","천","","십","백","천","","십","백","천","","십","백","천");
    var result = "";
    for(i=0; i<num.length; i++) {
        str = ""; han = hanA[num.charAt(num.length-(i+1))];

        if(han != "") str += han+danA[i];
        if(i == 4) str += "만";
        if(i == 8) str += "억";
        if(i == 12) str += "조";
        result = str + result;
    }

    if(num != 0) result = result + "원"; return result ;
}

function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
        currentDate = Date.now();
    } while (currentDate - date < milliseconds);
}
/**
 * 좌측문자열채우기
 * @params
 *  - padLen : 최대 채우고자 하는 길이
 *  - padStr : 채우고자하는 문자(char)
 */
String.prototype.lpad = function(padLen, padStr) {
    var str = this;
    if (padStr.length > padLen) {
        console.log("오류 : 채우고자 하는 문자열이 요청 길이보다 큽니다");
        return str + "";
    }
    while (str.length < padLen)
        str = padStr + str;
    str = str.length >= padLen ? str.substring(0, padLen) : str;
    return str;
}

/**
 * 우측문자열채우기
 * @params
 *  - padLen : 최대 채우고자 하는 길이
 *  - padStr : 채우고자하는 문자(char)
 */
String.prototype.rpad = function(padLen, padStr) {
    var str = this;
    if (padStr.length > padLen) {
        console.log("오류 : 채우고자 하는 문자열이 요청 길이보다 큽니다");
        return str + "";
    }
    while (str.length < padLen)
        str += padStr;
    str = str.length >= padLen ? str.substring(0, padLen) : str;
    return str;
}


function setYn(srcChkId, srcId){
	var srcChk = $("#" + srcChkId).is( ":checked") ? 'Y' : 'N';
	$("#" + srcId).val(srcChk);
}

function setChecked(srcChkId, srcId){
	var isChecked = $("#" + srcId).val() == 'Y' ;    	
    var srcChk = $("#" + srcChkId).attr('checked', isChecked);
}

function setActive(srcChkId, srcId){
	var srcChk = $("#" + srcChkId).is( ":checked") ? '9999' : '0000';
	$("#" + srcId).val(srcChk);
}

function setActiveChecked(srcChkId, srcId){
	var isChecked = $("#" + srcId).val() == '9999' ;
	if(isChecked){
		var srcChk = $("#" + srcChkId).attr('checked', 'checked');
	}
}

function showModal(msg, isConfirm, fn){
    var str = "";
    str = str + " <div class='modal fade' tabindex='-1' role='dialog' aria-hidden='true' id='_modal'>       " ;
    str = str + "   <div class='modal-dialog modal-sm'>                                                     " ;
    str = str + "     <div class='modal-content'>                                                           " ;
	str = str + "                                                                                           " ;
    str = str + "       <div class='modal-header'>                                                          " ;
    str = str + "         <h6 class='modal-title' id='myModalLabel2'>알림</h6>                                " ;
    str = str + "         <button type='button' class='close' data-dismiss='modal' aria-label='Close'>      " ;
	str = str + "         <span aria-hidden='true'>×</span>                                                 " ;
    str = str + "         </button>                                                                         " ;
    str = str + "       </div>                                                                              " ;
    str = str + "       <div class='modal-body'>                                                            " ;
    str = str + "         <h6>" + msg + "</h6>                                                              " ;
    str = str + "       </div>                                                                              " ;
    str = str + "       <div class='modal-footer'>                                                          " ;
    
    if(isConfirm){
    	str = str + "         <button type='button' class='btn btn-primary' id='_modal_confirm' >확인</button>     " ;
    }
    
	str = str + "		  <button type='button' class='btn btn-secondary' data-dismiss='modal'>닫기</button>  " ;                       
    str = str + "       </div>                                                                              " ;
    str = str + "     </div>                                                                                " ;
    str = str + "   </div>                                                                                  " ;
    str = str + " </div>                                                                                    " ;
	

	
	if($("#_modal").length){
		$("#_modal").remove();
	}	
	
	$("body").append(str);
	
	if(isConfirm){
		$("#_modal_confirm").on("click", function(){
			fn();
			$('#_modal').modal("hide"); //닫기
		}
		);
		 
	}
	
	$('#_modal').modal("show");	
}

function showConfirm(msg, fn){
	showModal(msg, true, fn);
}

function getPageDiv(paging){

  var pageDiv = "";
  if(paging == null) return;
  
  var previosPage = paging.startPage - 1;
  var perPage = paging.displayPageNum;
  var nextPage = paging.endPage + 1;
  var startPage = paging.startPage;
  var endPage = paging.endPage;
  var currPage = paging.page;	  
	  
  pageDiv = pageDiv + "<div class='row'>                                                ";
  pageDiv = pageDiv + "  <div class='btn-group' role='group'>                           ";
  
  if(paging.prev){    	  
  	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' onClick='search(" + previosPage + ", " + perPage + ")'>이전</button>  ";
  }else{
  	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' disabled>이전</button>  ";
  }
  
  for( i = startPage ; i <= endPage ; i++ ){
	  if(currPage == i){
		 pageDiv = pageDiv + "<button type='button' class='btn btn-secondary' disabled>" + i + "</button>";    		  
	  }else{    		  
	  	pageDiv = pageDiv + "<button type='button' class='btn btn-secondary' onClick='search(" + i + ", " + perPage + ")'>" + i + "</button>   ";
	  }
	  
  }
  
  if(paging.next){    	  
   	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' onClick='search(" + nextPage + ", " + perPage + ")'>다음</button>  ";
   }else{
   	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' disabled>다음</button>  ";
   }
  
  
  pageDiv = pageDiv + "  </div>                                                         ";
  pageDiv = pageDiv + "</div>                                                           ";
  
  return pageDiv;
}


	
	
function getYoutubeId(url) {
    var tag = "";
    if(url)  {
        var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        var matchs = url.match(regExp);
        if (matchs) {
            tag = matchs[7];
        }
        return tag;
    }
}	

function checkFeature(chkBox){
	
	console.log("checkFeature : " + chkBox.name + " checked : " + chkBox.checked);
	
	var fId = "";	
	var fVal = "";
	var feature = null;
	var otherValue = null;
	for(var i = 0 ; i < 8 ; i++){
		fId = "featureList" + i + ".otherCode";
		feature = document.getElementById(fId);
		
		fVal = feature.value;
		console.log("feature code : " + fVal);
		if(fVal == chkBox.name){			
			otherValue = document.getElementById("featureList" + i + ".otherValue");			
			if(chkBox.checked){
			  otherValue.value = 1;	
			}else{
		      otherValue.value = 0;
			}
			
			break;
		}
	}
}

var categoryData = null;

function setCategoryData(inputCategory, category1, category2, optional){
	var selected = "";
	
	if( inputCategory == null) { // 최초 조회 시점
		$('#category1').empty();
		var isFirst = true;
		var option = null;
		
		if(optional){
			option = $("<option value='' >선택하세요</option>");			
			$('#category1').append(option);
		}
		
		$.each(categoryData, function(index, item) {
			if( isFirst ){				
				inputCategory = item.middleId;
				isFirst = false;
				console.log(item.middleName);
			}
			
			option = $("<option value='" + item.middleId + "' >"+item.middleName+"</option>");
			
			$('#category1').append(option);
		});
	}
	
	if(category1 != null){
		inputCategory = category1; 
		$('#category1').val(category1).prop("selected","selected");
	}		
	
	
	var firstVal = $('#category2 option:eq(0)').val();
 
    $('#category2').empty();
    selected = "";
	
	if(optional || firstVal == ""){
		option = $("<option value='' >선택하세요</option>");			
		$('#category2').append(option);
	}
		
	$.each(categoryData, function(index, item) {
		if(inputCategory == item.middleId){
			console.log("대상 분류 : " + item.middleName);
			
			$.each(item.subClassList, function(index, item2) {
				// console.log(item2.subName);
				
				option = $("<option value='" + item2.subId + "' >"+item2.subName+"</option>");
				$('#category2').append(option);
				
			});
		}
	});
	
	console.log("category2 : " + category2);
	if(category2 != null){
		$('#category2').val(category2).prop("selected","selected");
	}
}

function category1Change(){
	var inputCategory = $('#category1 option:selected').val();
	
	setCategoryData(inputCategory);	
}

function getListCategoryData(category1, category2){
	reqCategoryData(category1, category2, true);
}

function getCategoryData(category1, category2){
	reqCategoryData(category1, category2);
}

function reqCategoryData(category1, category2, optional){
	var param = {};  
	
	$.ajax({
        url : "/w1/product/get_classdata",
        data : param,
        type : 'post',
        contentType:"application/json; charset=UTF-8",
        dataType:"json",
        async: false,
        success : function(data){
        	categoryData = data;
        	setCategoryData(null, category1, category2, optional);
        },
        
        error : function(e){
        	console.log(e);
        }
    });
	
}

function getPointCategoryData(category1, category2){
	reqPointCategoryData(category1, category2);
}

function reqPointCategoryData(category1, category2, optional){
	var param = {};  
	
	$.ajax({
        url : "/w1/product/get_point_class_data",
        data : param,
        type : 'post',
        contentType:"application/json; charset=UTF-8",
        dataType:"json",
        async: false,
        success : function(data){
        	categoryData = data;
        	setCategoryData(null, category1, category2, optional);
        },
        
        error : function(e){
        	console.log(e);
        }
    });
	
}




