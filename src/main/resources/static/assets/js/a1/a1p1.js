var path =  '/api/a1/p1';

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
            var url = "/a1/p1?page="+arg1;
            location.href=url;
            break;
        /*카테고리 목록*/
        case "L2":
            $.post(path+'/l1',{masterId : 4},function(res){
                var data = res.data;
                var idx = 1;

                data.forEach(function(ec){
                    $(".categoryCon").append('<div class="categorySub" id="cate_'+idx+'"><input type="hidden" name="updateIdList" id="id_'+idx+'" value="'+ec.id+'"/><input type="hidden" name="codeNameList" class="codeNameList" id="codeName_'+idx+'" value="'+ec.codeName+'"/><p class="categoryTit">'+ec.codeName+'</p><div class="modiBtn-box"><button type="button" class="categoryModi"></button><button type="button" class="categoryRemove"></button></div></div>');
                    idx++;
                });
                $('#commOt').addClass('on');

            });

            break;
        /*저장*/
        case "S1":

            var formS1 = $('#form1');
            var url = path+'/s1';

            modal.confirm("저장하시겠습니까?",function(){
                loading(1);

                $.post(url,formS1.serialize(),function(){
                    loading(0);
                    modal.alert('저장되었습니다.');
                    ut.redirect("/a1/p1","page",$("#page").val());
                });
            });
            break;
        /*대여*/
        case "S2":

            var formS1 = $('#form1');
            //var formData = new FormData(formS1[0]);
            var url = path+'/s2';

            modal.confirm("저장하시겠습니까?",function(){
                loading(1);
                $.ajax({
                    data : formS1.serialize(),
                    type : "POST",
                    url : url,
                    success : function(res) {
                        loading(0);
                        modal.alert('저장되었습니다.');
                        ut.redirect("/a1/p1","page",$("#page").val());
                    },error : function(request,status,error){
                        loading(0);
                    }
                });
            });
            break;
        /*카테고리 저장*/
        case "S3":
            var formS2 = $('#form3');
            modal.confirm("저장하시겠습니까?",function(){
                $.post(path+'/s3',formS2.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    ut.redirect("/a1/p1","page",$("#page").val());
                });
            });
            break;
        case "G1":

            break;
        case "E1":

            break;
        /*삭제*/
        case "D1":
                var formS1 = $('#form2');
                var url = path+'/d1';

                if($("input[name=seqList]:checked").length == 0){
                    modal.alert("체크한 데이터가 없습니다.");
                    return;
                }

                var idList = new Array();
                $("input[name=seqList]:checked").each(function(){
                        var numId = $(this).prop("id");
                        var num = numId.substring(1, numId.length);
                        var bookId = "id"+num;
                        idList.push($("#"+bookId).val());
                });

                $("#idList").val(idList);
                loading(1);
                $.post(url,formS1.serialize(),function(){
                    loading(0);
                    modal.alert('삭제되었습니다.');
                    location.reload();
                });
                break;
    }
}

