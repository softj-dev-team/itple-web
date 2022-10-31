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

            modal.confirm("저장하시겠습니까?", function () {

                $.post(url, formS1.serialize(), function () {
                    loading(0);
                    modal.alert('저장되었습니다.');
                    ut.redirect("/a7/p1", "academyType", arg1);
                });
            });
            break;
        case "L2":
            var url = path +"/p1/l2";

            $.post(url, {"page":0} ,function(res){
                var idx = 1;
                $("#searchM .modalTable .admodalT .checklist2 tr").remove();
                var data = res.data;
                var html = "";
                /*data.forEach(el => function(){
                    html ="<tr>\n" +
                    "<td><input type=\"checkbox\" id=\"mCh_"+idx+"\"><label for=\"mCh_"+idx+"\"></label></td>\n" +
                    "<td>힉셍 :"+data.user.userName+"</td>\n" +
                    "<th>"+data.telNo+"</th>\n" +
                    "</tr>\n" +
                    "<tr>\n";
                    idx += 1;
                    html += "<td><input type=\"checkbox\" id=\"mCh_"+idx+"\"><label for=\"mCh_"+idx+"\"></label></td>\n" +
                    "<td>부모님 :"+data.parentTel+"</td>\n" +
                    "<th>"+data.telNo+"</th>\n" +
                    "</tr>\n";
                    idx++;
                });*/
                //$("#searchM .modalTable .admodalT .checklist2").append(html);
            });

            break;
        // 메세지 상세정보
        case "P1":
            $('#smsModal .modalBody').html("");
            var url = path + '/p1/p1';
            var mid = $("#mid"+arg1).val();
            var message = $("#msg"+arg1).val();
            $.post(url, {"mid" : mid}, function(res){
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
        case "E1":
            break;
        case "D1":
            break;
    }
}

