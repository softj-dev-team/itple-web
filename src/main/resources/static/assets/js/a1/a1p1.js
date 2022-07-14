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
            var url = path+"?page="+arg1;
            location.href=url;
            break;
        /*저장*/
        case "S1":

            var formS1 = $('#form1');
            var url = '/api'+ path+'/s1';

            modal.confirm("저장하시겠습니까?",function(){
                loading(1);

                $.post(url,formS1.serialize(),function(){
                    loading(0);
                    modal.alert('저장되었습니다.');
                    var page = $("#page").val();
                    goAction("L1", page);
                });
            });
            break;
        /*대여*/
        case "S2":

            var formS1 = $('#form1');
            //var formData = new FormData(formS1[0]);
            var url = '/api'+ path+'/s2';

            modal.confirm("저장하시겠습니까?",function(){
                loading(1);
                $.ajax({
                    data : formS1.serialize(),
                    type : "POST",
                    url : url,
                    success : function(res) {
                        loading(0);
                        modal.alert('저장되었습니다.');
                        var page = $("#page").val();
                        goAction("L1", page);
                    },error : function(request,status,error){
                        loading(0);
                    }
                });
            });
            break;
        case "Q1":

            break;
        case "E1":

            break;
        /*삭제*/
        case "D1":
                var formS1 = $('#form2');
                var url = '/api'+ path+'/d1';

                if($("input[name=seqList]:checked").length == 0){
                    modal.alert("체크한 데이터가 없습니다.");
                    return;
                }
                loading(1);
                var idList = new Array();
                $("input[name=seqList]:checked").each(function(){
                        var numId = $(this).prop("id");
                        var num = numId.substring(1, numId.length);
                        var bookId = "id"+num;
                        idList.push($("#"+bookId).val());
                });

                $("#idList").val(idList);
                $.post(url,formS1.serialize(),function(){
                    loading(0);
                    modal.alert('삭제되었습니다.');
                    var page = $("#page").val();
                    goAction("L1", page);
                });
                break;
        /*팝업*/
        case "P1":
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

