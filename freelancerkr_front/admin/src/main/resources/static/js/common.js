
function removeComma(n) {  // 콤마제거
    if ( typeof n == "undefined" || n == null || n == "" ) {
        return "";
    }
    var txtNumber = '' + n;
    return txtNumber.replace(/(,)/g, "");
}

function numberWithCommas(n) {
    // console.log(n)
    if ( typeof n == "undefined" || n == null || n == "" ) {
        return "";
    }
    n.value = n.value.replace(/^0+/, '');
    var rgx1 = /\D/g;  // /[^0-9]/g 와 같은 표현
    var parts=n.value.toString().split(".");
    parts[0] = parts[0].replace(rgx1,"");
    n.value =  parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + ((parts.length == 2)?'.'+parts[1]:'');
}