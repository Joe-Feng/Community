$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

//置顶
function setTop() {
    $.post(
        CONTEXT_PATH +"/discuss/top/" + $("#topBtn").val(),
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
             location.reload();
            }else{
                alert(data.msg);
            }
        }
    );
}

//加精
function setWonderful() {
    $.post(
        CONTEXT_PATH +"/discuss/wonderful/" + $("#wonderfulBtn").val(),
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                location.reload();
            }else{
                alert(data.msg);
            }
        }
    );
}

//删除
function setDelete() {
    $.post(
        CONTEXT_PATH +"/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
               location.href = CONTEXT_PATH + "/index";
            }else{
                alert(data.msg);
            }
        }
    );
}

