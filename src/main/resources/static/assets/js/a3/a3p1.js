var path =  '/api/a3/p1';

$(function(){
    $("#form1 input").on("keyup",function(e){
        if(e.keyCode == 13){
            goSearch();
        }
    });

    $("#studentTaskId").on("change",function(){
        var studentTaskId = $(this).val();
       if(studentTaskId != ''){
           ut.redirect(`/a3/p1-detail/${studentTaskId}`);
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
                var userId = "id"+num;
                idList.push($("#"+userId).val());
            });

            $("#idList").val(idList);

            $.post(url,formS1.serialize(),function(){
                loading(0);
                modal.alert('삭제되었습니다.');
                location.reload();
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
        /*엑셀*/
        case "E1":
            loading(1);
            $.fileDownload("/excelFileDownload/a3/p1/e1",{
                httpMethod: "POST",
                data:$("#form1").serialize(),
                successCallback: function (url) {
                    loading(0);
                    modal.alert("파일다운로드에 성공하였습니다.");
                    location.reload();
                },
                failCallback: function(responesHtml, url) {
                    loading(0);
                    modal.alert("파일다운로드에 실패하였습니다.");
                    location.reload();
                }
            });
            break;
    }
}

function setStudentList(){
    $("#studentList").empty();
    var classId = $("select[name=classId]").val();
    $.post("/api/classStudentList",{id: classId}, function(res) {
        var list = res.data;
        var html = "";

        if(ut.isEmpty(list)){
            return;
        }

        list.forEach(function(el){
            html += `<input type="checkbox" id="stud${el.id}" name="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
        });
        $("#studentList").html(html);
    });
}