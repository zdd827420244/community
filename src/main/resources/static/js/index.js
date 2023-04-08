$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");  //隐藏发布框

	//获取标题和内容
	var title=$("#recipient-name").val();            //$("#")是jQuery id选择器，里面填id  .val()表示得到框里的值
	var content=$("#message-text").val();
	//发布异步请求POST
	$.post(
		CONTEXT_PATH+ "/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data=$.parseJSON(data);
			//在提示框中显示返回的消息
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");       //显示提示框
			setTimeout(function(){       //2秒后提示框隐藏
				$("#hintModal").modal("hide");
				//刷新/重载页面
				if(data.code==0) {
					window.location.reload();
				}
			}, 2000);

		}
	);


}