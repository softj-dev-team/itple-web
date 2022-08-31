var path =  '/api/a5/p1';

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

function goAction(flag, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11) {
    var form = $('#form1');

    switch (flag) {
        /*목록*/
        case "L1":
            location.href=path;
            break;
        /*저장*/
        case "S1":

            var url = path+'/s1';

            modal.confirm("저장하시겠습니까?",function(){
                $.post(url, {id:arg1, studentId:arg2, cost:arg3, paymentDate:arg4, paymentType:arg5, memo:arg6, year:arg7, month:arg8, paymentDay:arg9, price:arg10, paymentStatus:arg11},function(){
                    loading(0);
                    modal.alert('저장되었습니다.');
                    location.reload();
                });
            });
            break;
        /*완납처리*/
        case "S2":

            var url = path+'/s2';

            modal.confirm("완납처리 하시겠습니까?",function(){
                var param1 = $("#studentIdList").val();
                var param2 = $("#paymentIdList").val();
                var param3 = $("#paymentDayList").val();
                var param4 = $("#priceList").val();

                $.post(url, {studentIdList:param1, paymentIdList:param2, paymentDayList:param3, priceList:param4, year:arg1, month:arg2, paymentDate:arg3, paymentType:arg4},function(){
                    loading(0);
                    modal.alert('저장되었습니다.');
                    location.reload();
                });
            });

            break;
        case "E1":

            break;
        case "D1":
            var url = path+'/d1';

            modal.confirm("미납처리 하시겠습니까?",function(){

                var param1 = $("#paymentIdList").val();

                $.post(url, {paymentIdList:param1},function(){
                    loading(0);
                    modal.alert('저장되었습니다.');
                    location.reload();
                });
            });

            break;
        /*팝업*/
        case "P1":
            $('#noticeForm textarea').each(function e(){$(this).val('');});
            $('#noticeForm select').val(9999).change();
            $('#noticeForm input[name=seq]').val(0);
            if(arg1){
                $.post(path+'/p1',{seq: arg1},function(res){
                    loading(0);
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

