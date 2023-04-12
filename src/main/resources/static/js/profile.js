$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this; //获取当前按钮
	if($(btn).hasClass("btn-info")) {  //判断按钮是否是蓝色样式
		// 关注TA
		$.post(
			CONTEXT_PATH + "/follow",
			{"entityType":3,"entityId":$(btn).prev().val()}, //.prev()访问前一个节点  val()访问值
			function(data) {
				data = $.parseJSON(data);
				if(data.code == 0) {
					window.location.reload();
				} else {
					alert(data.msg);
				}
			}
		);
		// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		$.post(
			CONTEXT_PATH + "/unfollow",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function(data) {
				data = $.parseJSON(data);
				if(data.code == 0) {
					window.location.reload();
				} else {
					alert(data.msg);
				}
			}
		);
		//$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}