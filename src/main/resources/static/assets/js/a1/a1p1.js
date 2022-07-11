var path =  '/a1/p1';

$(function(){
    $("#form1 input").on("keyup",function(e){
        if(e.keyCode == 13){
            goSearch();
        }
    })
});

function goSearch(){
    $("#page").val(0);
    $("#form1").submit();
}

function goReset(flag){
    $('#form1 input,#form1  select').each(function e(){$(this).val('');});
    goSearch();
}

function reload(){
    window.location.reload();
}

function goAction(flag, arg1, arg2) {
    var form = $('#form1');

    switch (flag) {
        /*목록*/
        case "L1":
            location.href=path;
            break;
        /*저장*/
        case "S1":
            var formS1 = $('#form1');
            modal.confirm(null,"저장하시겠습니까?",function(){
                loading(1);
                var url = '/api'+ path+'/s1';
                if(arg1 != null && arg1 != ""){
                    url = url + "/" + arg1
                }

                var formData = new FormData(formS1[0]);
                console.log(formData);
                $.ajax({
                    data : formData,
                    type : "POST",
                    url : url,
                    contentType : false,
                    processData : false,
                    success : function(res) {
                        modal.alert('저장되었습니다.');
                        loading(0);
                    }
                });

               /* $.post(url,formS1.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    loading(0)
                    reload();
                });*/
            });
            break;
        case "Q1":

            break;
        case "E1":

            break;
        /*삭제*/
        case "D1":
            var formS1 = $('#form2');

            if($("input[name=seqList]:checked").length == 0){
                modal.alert("체크한 데이터가 없습니다.");
                return;
            }
            var url = '/api'+ path+'/d1';

            modal.confirm(null,"체크한 데이터를 삭제하시겠습니까?",function(){
                loading(1);
                $.post(url,formS1.serialize(),function(){
                    modal.alert('삭제되었습니다.');
                    reload();
                });
            });

            break;
        /*팝업*/
        case "P1":
            // $('#noticeForm input').each(function e(){$(this).val('');});
            $('#noticeForm textarea').each(function e(){$(this).val('');});
            $('#noticeForm select').val(9999).change();
            $('#noticeForm input[name=seq]').val(0);
            if(arg1){
                $.post(path+'/p1',{seq: arg1},function(res){
                    var el = res.data;
                    $("textarea[name=content]").val(el.content);
                    $('#noticeForm select').val(el.accessDept.seq).change();
                    $('#noticeForm input[name=seq]').val(el.seq);
                    if(loginRole=='ROLE_1' || loginRole=='ROLE_2'){
                        $("#p1Save").show();
                    }else if(loginRole=='ROLE_3' && loginDeptSeq == el.accessDept.seq){
                        $("#p1Save").show();
                    }else{
                        $("#p1Save").hide();
                    }
                });
            }
            modalOpen('notice-write');
            break;
    }
}

