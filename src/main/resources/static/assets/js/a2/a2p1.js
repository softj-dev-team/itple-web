var path =  '/api/a2/p1';

$(function(){
    $("#form1 input").on("keyup",function(e){
        if(e.keyCode == 13){
            goSearch();
        }
    });

    $("#studentTaskId").on("change",function(){
        var studentTaskId = $(this).val();
       if(studentTaskId != ''){
           ut.redirect(`/a2/p1-detail/${studentTaskId}`);
       }
    });
})

function goSearch(){
    $("#page").val(0);
    $("#form1").submit();
}

function goReset(flag){
    $('#form1 input,#form1  select').each(function e(){$(this).val('');});
    goSearch();
}

function goAction(flag, arg1, arg2, arg3, arg4) {
    var form = $('#form1');

    switch (flag) {
        /*목록*/
        case "L1":
            location.href=path;
            break;
        /*저장*/
        case "S1":
            $("input[name=status]").val(arg1);
            var formS1 = $('#form1');
            modal.confirm("저장하시겠습니까?",function(){
                $("input[name=idList]").prop("checked",true);
                $.post(path+'/s1',formS1.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    if(arg1 != 'NOT_SUBMIT') {
                        ut.redirect("/a2/p1","taskType",$("input[name=taskType]").val());
                    }
                });
            });
            break;
        case "S2":
            modal.confirm("확인 하시겠습니까?",function(){
                $.post(path+'/s2',{id: arg1},function(res){
                    location.reload();
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
                    ut.redirect('/a2/p1');
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
            $.post(path+'/p1',{id: arg1},function(res){
                var el = res.data;
                console.log(el);
                $("#reportMo .modalGroup").html(`[${el.student.academyClass.className}]`);
                $("#reportMo .modalTit").html(el.task.subject);
                $("#reportMo .modalName").html(el.student.user.userName);
                $("#reportMo .modalMain").html(el.contents);
                $("#reportMo .fileUP").empty();
                if(el.status == 'SUBMIT') {
                    $("#reportMo .modalBtnWrap").show();
                    $("#completeBtn").attr("onclick",`goAction('S2',${el.id})`);
                }else{
                    $("#reportMo .modalBtnWrap").hide();
                }

                modalOpen('reportMo');

                var fileList = el.studentTaskFileList;
                if(ut.isEmpty(fileList)){
                    return;
                }
                fileList.forEach(function(el){
                    var html = `<li><a href="/studentTaskFileDownload/${el.uploadFileName}">${el.orgFileName}</a></li>`;
                    $("#reportMo .fileUP").append(html);
                })
            });
            break;
    }
}