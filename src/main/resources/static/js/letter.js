$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 获取接收消息者的名字
	var toName = $("#recipient-name").val();
	// 获取发送的消息内容
	var content = $("#message-text").val();

	// 发送ajax异步请求
	$.post(
		// 请求路径
		CONTEXT_PATH + "/letter/send",

		// 请求参数
		{"toName":toName, "content":content},

		// 回调函数
		function (data) {
			// 把data转换为js对象
			data = $.parseJSON(data);

			if (data.code == 0) {
				// 添加提示信息到提示框
				$("#hintBody").text("发送成功!");
			} else {
				$("#hintBody").text(data.msg);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 重新加载页面
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}