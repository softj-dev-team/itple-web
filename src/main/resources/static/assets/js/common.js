window.addEventListener("load", function(){
    setTimeout(loaded, 100);

}, false);

function loaded(){
    window.scrollTo(0, 1);
}

function handleFirstTab(e) {
  if (e.keyCode === 9) {
    document.body.classList.add('user-is-tabbing');

    window.removeEventListener('keydown', handleFirstTab);
    window.addEventListener('mousedown', handleMouseDownOnce);
  }
}

function handleMouseDownOnce() {
  document.body.classList.remove('user-is-tabbing');

  window.removeEventListener('mousedown', handleMouseDownOnce);
  window.addEventListener('keydown', handleFirstTab);
}

window.addEventListener('keydown', handleFirstTab);
// hader
$('.ham-btn').click(function(){
    $('.ham').addClass('on');
});
$('.ham-close').click(function(){
    $('.ham').removeClass('on');
    $('body').attr('style', '');
});
$('.bot-btn').click(function(){
    $(this).toggleClass('on');
    $(this).next('a').toggleClass('on');
    $(this).next().next().toggleClass('on');
});
//체크박스
$(function(){
    $('#all-chk').click(function(){
        var chk = $(this).is(':checked');//.attr('checked');
        if(chk) $('.checklist td:nth-child(1) input').prop('checked',true);
        else $('.checklist td:nth-child(1) input').prop('checked',false);
    });
});
$(function(){
    $('#studAll').click(function(){
        var chk = $(this).is(':checked');
        if(chk) $('.studCh input').prop('checked',true);
        else $('.studCh input').prop('checked',false);
    });
});
//파일첨부
var uploadFile = $('.fileBox .uploadBtn');
uploadFile.on('change', function(){
    if(window.FileReader){
        var filename = $(this)[0].files[0].name;
    } else {
        var filename = $(this).val().split('/').pop().split('\\').pop();
    }
    $(this).siblings('.fileName').val(filename);
});
$('.file-up-con .cancel-btn').click(function(){
    $(this).parent().remove();
})

//팝업닫기
$('.cancelbtn, .close-btn').click(function(){
    $(this).parents('.popup').removeClass('on')
});
$(document).on("click", ".modalClose, .modalOff, .modal-bg", function(){
    $(this).parents('.modal').removeClass('on');
});

//네비,카테고리
$('.ham-btn').click(function(){
    $('.category').addClass('on');
    $('body').attr('style', 'overflow:hidden');
  });
$('.cate-close, .cate-bg').click(function(){
    $('.category').removeClass('on');
    $('body').attr('style', '');
});

//팝업 열기 닫기
function popupshow(){
    $("body").addClass("popup-show");
}
function popupclose(){
    $("body").removeClass("popup-show");
}

$('.src-btn').on('click', function (e){
    if(!$(this).hasClass('active')){
        e.preventDefault()
        $('.src-btn').addClass('active');
        $('.search-in').addClass('active');
    }

});

function loading(isLoding){
    var isShow = isLoding ? 'show' : 'hide';
    $.LoadingOverlay(isShow);
}