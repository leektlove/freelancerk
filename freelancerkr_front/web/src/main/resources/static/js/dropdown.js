// $(".dropdown-item").click(function(){
$(".item_detail").click(function(){
    console.log($(this));
    console.log($(this)[0]);

    var domElem = $(this)[0];

    if(domElem.className === 'checkmark item_detail') {
      console.log($(this)[0]);
      // $(this).parent().find("input[type='checkbox']").prop("checked");
      if($(this).parent().find("input[type='checkbox']").prop("checked"))
        $(this).parent().find("input[type='checkbox']").prop("checked",false);
      else
        $(this).parent().find("input[type='checkbox']").prop("checked",true);
    }

    if(domElem.className === 'chk_container item_detail') {
      console.log($(this)[0]);
      // $(this).parent().find("input[type='checkbox']").prop("checked");
      if($(this).find("input[type='checkbox']").prop("checked"))
        $(this).find("input[type='checkbox']").prop("checked",false);
      else
        $(this).find("input[type='checkbox']").prop("checked",true);
    }

    console.log("return false");
    return false;
});

$(".checkmark_rounded").hover(
  function() {
    $(this).siblings(".sector").eq(0).css({"background-color": "#53A1EA"});
    $(this).siblings(".title").css({"color": "#FFFFFF"});
    $(this).css("background-color", "#53A1EA");
  }, function() {
    $(this).siblings(".sector").eq(0).css({"background-color": "#FFFFFF", "color": "#000000"});
    $(this).siblings(".title").css({"color": "#000000"});
    $(this).css("background-color", "#FFFFFF");
  }
);
