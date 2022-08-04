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
            var subject = $("input[name=subject]").val();
            var teacher = $("input[name=teacher]").val();
            var contents = $("textarea[name=contents]").val();
            var startDate = $("input[name=startDate]").val();
            var endDate = $("input[name=endDate]").val();
            var coin = $("input[name=coin]").val();
            var taskType = $("input[name=taskType]").val();

            if(ut.isEmpty(subject)){
                modal.required("제목");
                return;
            }
            if(taskType == 'BOOK_REPORT' && ut.isEmpty(teacher)){
                modal.required("출제 선생님");
                return;
            }
            if(ut.isEmpty(contents)){
                modal.required("내용");
                return;
            }
            if(ut.isEmpty(startDate) || ut.isEmpty(endDate)){
                modal.required("독후감 마감일");
                return;
            }
            if(!ut.isValidDateRange(startDate, endDate)){
                modal.alert("독후감 마감일을 확인해주세요.");
                return;
            }
            if(ut.isEmpty(coin)){
                modal.required("지급포인트");
                return;
            }
            var formS1 = $('#form1');
            modal.confirm("저장하시겠습니까?",function(){
                $.post(path+'/s1',formS1.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    ut.redirect("/a2/p1","taskType", $("input[name=taskType]").val());
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
        case "S3":
            modal.confirm("다시제출 처리 하시겠습니까?",function(){
                var returnMessage = $("#reportMo #returnMessage").val();
                $.post(path+'/s3',{id: arg1, returnMessage: returnMessage},function(res){
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
        /*과제 삭제*/
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
                var taskId = "id"+num;
                idList.push($("#"+taskId).val());
            });

            $("#idList").val(idList);
            loading(1);
            $.post(url,formS1.serialize(),function(){
                loading(0);
                modal.alert('삭제되었습니다.');
                location.reload();
            });
            break;
        /*학생 과제 삭제*/
        case "D2":
            var formS1 = $('#form2');
            var url = path+'/d2';

            if($("input[name=seqList]:checked").length == 0){
                modal.alert("체크한 데이터가 없습니다.");
                return;
            }

            var idList = new Array();
            $("input[name=seqList]:checked").each(function(){
                var numId = $(this).prop("id");
                var num = numId.substring(1, numId.length);
                var taskId = "id"+num;
                idList.push($("#"+taskId).val());
            });

            $("#idList").val(idList);
            loading(1);
            $.post(url,formS1.serialize(),function(){
                loading(0);
                modal.alert('삭제되었습니다.');
                location.reload();
            });
            break;
        /*팝업*/
        case "P1":
            $.post(path+'/p1',{id: arg1},function(res){
                var el = res.data;

                $("#reportMo .modalGroup").html(`[${el.student.academyClass.className}]`);
                $("#reportMo .modalTit").html(el.task.subject);
                $("#reportMo .modalName").html(el.student.user.userName);
                $("#reportMo .modalMain").html(el.contents);

                var caracteres = $("#reportMo .modalMain").text();
                var totalCaracteres = caracteres.length;
                $(".text-count").text(totalCaracteres);

                $("#reportMo .fileUP").empty();

                if(el.status == 'SUBMIT') {
                    $("#reportMo .modalBtnWrap").show();
                    $("#rejectBtn").attr("onclick",`goAction('S3',${el.id})`);
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