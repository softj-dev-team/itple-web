var path =  '/api/a8';

$(function(){
    $("#form1 input").on("keyup",function(e){
        if(e.keyCode == 13){
            goSearch();
        }
    })
});

function goSearch(){
    $("#page").val(0);
    $("#pageNum").val(0);
    $("#pageOffset").val(0);
    $("#prevPage").val(0);
    $("#nextPage").val(0);
    $("#prevPageOffset").val(0);
    $("#nextPageOffset").val(0);
    $("#savePrevPage").val(0);
    $("#savePrevOffset").val(0);
    $("#savePrePrevPage").val(0);
    $("#savePrePrevOffset").val(0);
    $("#savePrePrevPage2").val(0);
    $("#savePrePrevOffset2").val(0);
    $("#prev").val("");
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
            var url = "/a8/p1?page=" + arg1;
            location.href = url;
            break;
        /*메세지전송*/
        case "S1":

            var formS1 = $('#form1');
            var url = path + '/p1/s1';

            modal.confirm("전송하시겠습니까?", function () {

                $.post(url, formS1.serialize(), function () {
                    loading(0);
                    modal.alert('전송되었습니다.');
                    ut.reload();
                });
            });
            break;
        // 메세지 상세정보
        case "P1":
            $('#smsModal .modalBody').html("");
            var url = path + '/p1/p1';
            var mid = $("#mid"+arg1).val();
            var type = $("#type"+arg1).val();
            var message = $("#msg"+arg1).val();
            $.post(url, {"mid" : mid, "type" : type}, function(res){
                var data = res.data;
                var html = "";
                for(var i=0; i< data.length; i++){

                    var parentName = "";
                    var parentTel = "";

                    html += "<label for=\"\" class=\"mb5\">수신번호</label>\n" +
                        "                <p>"+data[i].receiver+"</p>\n";

                    var studentArr = data[i].studentList;
                    for(var j=0; j<studentArr.length; j++){

                        html += "            <label for=\"\" class=\"mb5\">이름</label>\n" +
                            "                <p>"+studentArr[j].userName+"</p>\n" +
                            "                <label for=\"\" class=\"mb5\">연락처</label>\n" +
                            "                <p>"+studentArr[j].telNo+"</p>\n";
                        parentName = studentArr[j].parentName;
                        parentTel = studentArr[j].parentTel;
                    }

                    html += "            <label for=\"\" class=\"mb5\">부모님성함</label>\n" +
                        "                <p>"+parentName+"</p>\n" +
                        "                <label for=\"\" class=\"mb5\">부모님연락처</label>\n" +
                        "                <p>"+parentTel+"</p>\n" +
                        "                <label for=\"\" class=\"mb5\">전송상태</label>\n" +
                        "                <p>"+data[i].smsState+"</p>\n";
                }

                html += "           <label for=\"\" class=\"mb5\">내용</label>\n" +
                    "                <p>\n" + message +
                    "                </p>" +
                    "               <div class=\"modalBtnWrap\">\n" +
                    "                    <button type=\"button\" class=\"btn btn-blue modalOff\">확인</button>\n" +
                    "                </div>";

                $('#smsModal .modalBody').append(html);
                $('#smsModal').addClass('on');

            });
            break;
        case "P2":
            break;
        case "D1":
            break;
    }
}

