function timeformatter(value, row){
	return value.substr(0,4)+'-'+value.substr(4,2)+'-'+value.substr(6,2)+' '+value.substr(8,2)+':'+value.substr(10,2)+':'+value.substr(12,2)+'.'+value.substr(14,3)
}

function sendMsg(api,data,callback){	
	var reqdata=JSON.stringify(data);		
	$.ajax({
		type : "post",
		url : api,
		dataType : "json",
		data : reqdata,
		success : callback
	});
};

function OnAck(resp,tips){
	switch(resp.errorcode){
		case 0:alert(tips+"成功");break;
		case -1:alert(resp.errordesc);break;
		case 4096:alert("您未登录或者会话已超时");break;
		default:break;
	}
}

var handlePageElement = function() {	
	
	document.onkeydown = function (e) {
		var theEvent = window.event || e;
		var code = theEvent.keyCode || theEvent.which;
		if (code == 13 ) {
			$("#call_btn").click();
		}
	};
	
	var socket;
	if (!window.WebSocket) {
		window.WebSocket = window.MozWebSocket;
	}
	if (window.WebSocket) {
		var wsurl = "ws://" + window.location.hostname + ":9292/test";
		socket = new WebSocket(wsurl);
		socket.onmessage = function(event) {
			var json = eval('(' + event.data + ')'); 			
			console.log("SERVER message arrived:%s", event.data);
			var data = $('#table_info').bootstrapTable('getData');
			for(i = 0 ; i < data.length ; i++){
				var obj = data[i];
				if(json.callid == obj.callid){
					switch(json.status){
						case "alerting":
							obj.alertingtime = timeformatter(json.ts);
							break;
						case "connect":
							obj.connecttime = timeformatter(json.ts);
							break;
						case "disconnect":
							if((obj.disconnecttime != null) && (obj.disconnecttime !=""))return;
							obj.disconnecttime = timeformatter(json.ts);
							
							var data = {"token":$.cookie("token"),"callid":obj.callid};
							var reqdata=JSON.stringify(data);		
							$.ajax({
								type : "post",
								url : "/rs/record",
								dataType : "json",
								data : reqdata,
								async : false,
								success : function(resp){
									switch(resp.errorcode){
										case 0:obj.recordvoice='<audio src="'+ resp.url + '" controls="controls">Your browser does not support the audio tag.</audio>';break;
										default:break;
									}}
							});
							
							var data = {"token":$.cookie("token"),"callid":obj.callid};
							var reqdata=JSON.stringify(data);		
							$.ajax({
								type : "post",
								url : "/rs/dlrecord",
								dataType : "json",
								data : reqdata,
								async : false,
								success : function(resp){
									switch(resp.errorcode){
										case 0:obj.recorddl='<a href="'+ resp.url + '"><img src="assets/img/download.gif"/></a>';break;
										default:break;
									}}
							});
							
							break;
						default:
						break;
					}
					$('#table_info').bootstrapTable('updateRow', {index: i,row: obj});
				}
			}
		};
		socket.onopen = function(event) {
			
		};
		socket.onclose = function(event) {
			var wsurl = "ws://" + window.location.hostname + ":9292/test";
			socket = new WebSocket(wsurl);
		};
	} else {
		alert("你的浏览器不支持 WebSocket,请使用Google Chrome或者FireFox浏览！");
	}

};
var handleCallFunction = function() {	
	$("#call_btn").click(function(){
		sendMsg("rs/makecall",{"token":$.cookie("token"),"customertel":$("#ctel").val(),"experttel":$("#atel").val(),"callduration":$("#callduration").val()},function(resp){
			OnAck(resp,"呼叫");
			var e = new Object;
			//e.state = '';
			e.callid = resp.callid;
			e.atel = $("#atel").val();
			e.ctel = $("#ctel").val();
			e.alertingtime = '';
			e.connecttime = '';
			e.disconnecttime = '';
			e.recordvoice = '';
			e.recorddl = '';
			
			$('#table_info').bootstrapTable('append', e);			
		});
	});
	$("#disconnect_btn").click(function(){
		sendMsg("/rs/disconnect",{"token":$.cookie("token"),"customertel":$("#ctel").val()},OnAck(resp,"挂断"));
	});
	$("#hold_btn").click(function(){
		sendMsg("/rs/hold",{"token":$.cookie("token"),"dnnum":$("#ctel").val()},OnAck(resp,"呼叫保持"));
	});
	$("#unhold_btn").click(function(){
		sendMsg("/rs/unhold",{"token":$.cookie("token"),"dnnum":$("#ctel").val()},OnAck(resp,"呼叫保持解除"));
	});
};

var CallUtils = function() {
    "use strict";
    return {
        init: function() {
			handlePageElement();
            handleCallFunction();
        }
    }
} ()