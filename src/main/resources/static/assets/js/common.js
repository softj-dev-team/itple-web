$(function(){
    $("#chkAll").on("click",function(){
        $("input[name=idList]").prop("checked",$(this).prop("checked"));
    });

    $(document).ajaxSend(function myErrorHandler(event, xhr, ajaxOptions, thrownError) {
        loading(1);
    });
    $(document).ajaxError(function myErrorHandler(event, xhr, ajaxOptions, thrownError) {
        console.log(event, xhr, ajaxOptions, thrownError);
        modal.alert(JSON.parse(xhr.responseText).message);
    });
    $(document).ajaxComplete(function myCompleteHandler(event, xhr, ajaxOptions, thrownError) {
        loading(0);
        if(xhr.status == 200 && xhr.responseText.startsWith("<!DOCTYPE html>")){
            modal.alert("로그인이 필요합니다.<br>페이지 새로고침 하시면 로그인페이지로 이동합니다.");
        }
    });
});

/* window.addEventListener("load", function(){
    setTimeout(loaded, 100);

}, false); : 스크롤탑 : 스크롤 가장 위로 위치시키는 기능 막기 : 20221024 */

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
    $('#all-chk2').click(function(){
        var chk = $(this).is(':checked');//.attr('checked');
        if(chk) $('.checklist2 td:nth-child(1) input').prop('checked',true);
        else $('.checklist2 td:nth-child(1) input').prop('checked',false);
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

var modal = {
    alert: function (message, title = '알림') {
        $("#alert-modal .modalHd h3").html(title ? title : '');
        $("#alert-modal .modalText").html(message ? message : '');
        modalOpen('alert-modal');
    },
    required: function (txt) {
        this.alert(txt+" 을(를) 입력하세요.");
    },
    confirm: function (message, callback, title = '알림') {
        $("#confirm-modal .modalHd h3").html(title ? title : '');
        $("#confirm-modal .modalText").html(message ? message : '');
        $("#confirm-modal #confirm-callback-btn").off();
        $("#confirm-modal #confirm-callback-btn").on("click",function(){
            callback();
            modalCloseId('confirm-modal');
        });
        modalOpen('confirm-modal');
    }
};

function modalOpen(id){
    $("#"+id).addClass("on");
}
function modalCloseId(id){
    $("#"+id).removeClass("on");
}
function copy(url){
    ut.copyStr(url ? url : location.href);
    modal.alert("URL이 복사되었습니다.");
}

var regExp = {
    isId: function (asValue) {
        var regExp = /^[A-z]+[A-z0-9]{3,19}$/g;

        return regExp.test(asValue);
    },
    isNotId: function (asValue) {
        return !this.isId(asValue);
    },
    isEmail: function (asValue) {
        var regExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;

        return regExp.test(asValue);
    },
    isNotEmail: function (asValue) {
        return !this.isEmail(asValue);
    },
}