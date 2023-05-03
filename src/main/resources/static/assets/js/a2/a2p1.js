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
                $("input[name=idList]").prop("checked",true);
                $.post(path+'/s1',formS1.serialize(),function(res){

                    modal.alert('저장되었습니다.');
                    var id = $("#id").val();
                    if(id > 0){
                        location.reload();
                    }else {
                        ut.redirect("/a2/p1", "taskType", $("input[name=taskType]").val());
                    }
                });
            });
            break;
        case "S2_1":
            modal.confirm("확인 하시겠습니까?",function(){
                $.post(path+'/s2',{id: arg1, taskStatus : "COMPLETE"},function(res){
                    location.reload();
                });
            });
            break;
        case "S2_2":
            modal.confirm("취소 하시겠습니까?",function(){
                $.post(path+'/s2',{id: arg1, taskStatus : "SUBMIT"},function(res){
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
        case "S4": // 선생님/반 등록
            var formS1 = $('#form1');
            modal.confirm("저장하시겠습니까?",function(){
                $.post(path+'/s4',formS1.serialize(),function(res){
                    modal.alert('저장되었습니다.');
                    location.href="/a2/p1";
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
        /*선생님/반 삭제*/
        case "D3":
            modal.confirm("삭제 하시겠습니까?",function(){
                $.post(path+'/d3',{id: arg1},function(res){
                    location.href="/a2/p1";
                });
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
                $("#reportMo #studentTaskId").val(el.id);

                var caracteres = $("#reportMo .modalMain").text();
                var totalCaracteres = caracteres.length;
                $(".text-count").text(totalCaracteres);

                $("#reportMo .fileUP").empty();

                if(el.coinComp){
                    $("#plusBtn").removeClass("btn-blue");
                    $("#minusBtn").removeClass("btn-red");
                    $("#plusBtn, #minusBtn").addClass("btn-gray");
                    $("#plusBtn, #minusBtn").prop("disabled", true);
                }else{
                    $("#plusBtn").addClass("btn-blue");
                    $("#minusBtn").addClass("btn-red");
                    $("#plusBtn, #minusBtn").removeClass("btn-gray");
                    $("#plusBtn, #minusBtn").prop("disabled", false);
                }

                if(el.status == 'SUBMIT') {
                    $("#completeBtn").attr("onclick",`goAction('S2_1',${el.id})`);
                    $("#rejectBtn").attr("onclick",`goAction('S3',${el.id})`);
                    $("#plusBtn").css("display","inline-block");
                    $("#minusBtn").css("display","inline-block");
                    $("#rejectBtn").css("display","inline-block");
                    $("#completeBtn").css("display","inline-block");
                    $("#cancleBtn").css("display","none");
                }else{
                    $("#cancleBtn").attr("onclick",`goAction('S2_2',${el.id})`);
                    $("#plusBtn").css("display","none");
                    $("#minusBtn").css("display","none");
                    $("#rejectBtn").css("display","none");
                    $("#completeBtn").css("display","none");
                    $("#cancleBtn").css("display","inline-block");
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
function setStudentList1() {
    $("#studentList").empty();
    var classId = $("select[id=classSel]").val();
    $.post("/api/classStudentList", {id: classId}, function (res) {
        var list = res.data;
        var html = "";

        if (ut.isEmpty(list)) {
            return;
        }
        list.forEach(function (el) {
            html += `<input type="checkbox" id="stud${el.id}" class="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
        });
        $("#studentList").html(html);
    });
}
function setStudentList2() {
    $("#studentList2").empty();
    var classId2 = $("select[id=classSel2]").val();
    $.post("/api/classStudentList",{id: classId2}, function(res) {
        var list = res.data;
        var html = "";

        if(ut.isEmpty(list)){
            return;
        }
        list.forEach(function(el){
            html += `<input type="checkbox" id="stud${el.id}" class="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
        });
        $("#studentList2").html(html);
    });
}
function setStudentList(){
    $("#studentList").empty();
    var classId = parseInt($("select[id=classSel]").val());

    $.post("/api/classStudentList",{id: classId}, function(res) {
        var list = res.data;
        var html = "";

        if(ut.isEmpty(list)){
            return;
        }
        list.forEach(function(el){
            html += `<input type="checkbox" id="stud${el.id}" class="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
        });
        $("#studentList").html(html);
    });

    $("#studentList2").empty();
    var classId2 = parseInt($("select[id=classSel2]").val());
    $.post("/api/classStudentList",{id: classId2}, function(res) {
        var list = res.data;
        var html = "";

        if(ut.isEmpty(list)){
            return;
        }
        list.forEach(function(el){
            html += `<input type="checkbox" id="stud${el.id}" class="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
        });
        $("#studentList2").html(html);
    });
}

function setClassList(){
    var teacherId =  parseInt($("#teacherId").val());

    $("#classSel").empty();
    $.post("/api/a2/p1/l3", {"teacherId": teacherId}, function(res) {
        var list = res.data;
        var html = "";
        if(ut.isEmpty(list)){
            return;
        }
        var i=0;
        var firstClassId = "";
        list.forEach(function(el){
            html += `<option value="${el.id}"`;
            if(i==0){
                html += " selected ";
                firstClassId = el.id;
            }
            html += `>${el.className}</option>`;
            i++;
        });
        $("#classSel").html(html);

        $("#studentList").empty();
        $.post("/api/classStudentList",{id: firstClassId}, function(res) {
            var list = res.data;
            var html = "";

            if(ut.isEmpty(list)){
                return;
            }
            list.forEach(function(el){
                html += `<input type="checkbox" id="stud${el.id}" class="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
            });
            $("#studentList").html(html);
        });
    });

    $("#classSel2").empty();
    $.post("/api/a2/p1/l4", {"teacherId": teacherId}, function(res) {
        var list = res.data;
        var html = "";
        if(ut.isEmpty(list)){
            return;
        }
        var i=0;
        var firstClassId = "";
        list.forEach(function(el){
            html += `<option value="${el.id}"`;
            if(i==0){
                html += " selected ";
                firstClassId = el.id;
            }
            html += `>${el.className}</option>`;
            i++;
        });
        $("#classSel2").html(html);

        $.post("/api/classStudentList",{id: firstClassId}, function(res) {
            var list = res.data;
            var html = "";

            if(ut.isEmpty(list)){
                return;
            }
            list.forEach(function(el){
                html += `<input type="checkbox" id="stud${el.id}" class="studentIdList" value="${el.id}"><label for="stud${el.id}">${el.user.userName}</label>`;
            });
            $("#studentList2").html(html);
        });
    });
}
/*
function selectClassId(taskId){
    var academyClass = $("#academyClass").val();
    $("#classId option").each(function(){
        if($(this).val() == academyClass){
            $(this).prop("selected", true);
        }
    });

    setStudentList();

    $.post("/api/a2/p1/l1", {id: taskId}, function(res) {
        var list = res.data;

        for(var i=0; i<list.length; i++){
            $("input[name=studentIdList]").each(function(){
                $(this).prop("disabled",true);
                if($(this).val() == list[i].student.id){
                    $(this).prop("checked",true);
                }
            });
        }
    });
}*/

