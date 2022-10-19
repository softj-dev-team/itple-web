var path =  '/api/c1/p1';

$(function(){
    $("#form1 input").on("keyup",function(e){
        if(e.keyCode == 13){
            goSearch();
        }
    })
})

function goSearch(){
    $("#page").val(0);
    $("#form1").submit();
}

function goReset(flag){
    $('#form1 input,#form1  select').each(function e(){$(this).val('');});
    goSearch();
}

function goAction(flag, arg1, arg2, arg3, arg4, arg5) {
    var form = $('#form1');

    switch (flag) {
        /*목록*/
        case "L1":
            location.href=path;
            break;
        /*카테고리 목록*/
        case "L2":
            $.post(path+'/l1',{masterId : 1},function(res){
                var data = res.data;
                var idx = 1;

                data.forEach(function(ec){
                    $(".categoryCon").append('<div class="categorySub" id="cate_'+idx+'"><input type="hidden" name="updateIdList" id="id_'+idx+'" value="'+ec.id+'"/><input type="hidden" name="codeNameList" class="codeNameList" id="codeName_'+idx+'" value="'+ec.codeName+'"/><p class="categoryTit">'+ec.codeName+'</p><div class="modiBtn-box"><button type="button" class="categoryModi"></button><button type="button" class="categoryRemove"></button><button type="button" class="upOrder"></button><button type="button" class="downOrder"></button></div></div>');
                    idx++;
                });
                $('#commOt').addClass('on');

            });

            break;
        /*저장*/
        case "S1":
            var formS1 = $('#form1');
            modal.confirm("저장하시겠습니까?",function(){
                $("input[name=idList]").prop("checked",true);
                $.post(path+'/s1',formS1.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    ut.redirect("/c1/p1","boardType", $("[name=boardType]").val());
                });
            });
            break;
        /*댓 저장*/
        case "S2":
            var contents = "";

            if(arg5 == "enter"){
                contents = $(arg4).val();
            }else{
                contents = $(arg4).siblings('input[name="recontent"]').val();
            }

            if(ut.isEmpty(contents)){
                modal.required("내용");
                return;
            }
            $.post(path+'/s2',{id: arg1, upperId: arg2, commentId: arg3, contents: contents},function(res){
                location.reload();
            });
            break;
        /*카테고리 저장*/
        case "S3":
            var formS2 = $('#form2');
            modal.confirm("저장하시겠습니까?",function(){
                $.post(path+'/s3',formS2.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    ut.redirect("/c1/p1","boardType", $("[name=boardType]").val());
                });
            });
            break;
        case "U1":
            $.post(`${path}/u1`,{id:arg1},function(){
               location.reload();
            });
            break;
        case "E1":

            break;
        /*삭제*/
        case "D1":
            modal.confirm("삭제하시겠습니까?",function(){
                $.post(`${path}/d1`,{id:arg1},function(){
                    ut.redirect('/c1/p1', "boardType", $("#boardType").val());
                });
            });
            break;
        /*댓 삭제*/
        case "D2":
            modal.confirm("삭제하시겠습니까?",function(){
                $.post(`${path}/d2`,{id:arg1},function(){
                    location.reload();
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