$(document).ready(function() {

	// parse msg metadata from table
	function getMsgMetadata(btn){
		var metadata = {};
		var row = $(btn).parent().parent();

		metadata.id  = row.find('.msgId').html();
		metadata.timestamp = row.find('.msgTimestamp').html();
		metadata.read = row.find('.msgIsRead').html();

		return metadata;
	}

	// get and display msg
	function getMsg(msgMetadata){
		$.ajax({
			url: WEB_ROOT() + "/api/message/" + msgMetadata.id,
			headers: getCsrfHeaders(),
			success: function(data){
				drawModal(msgMetadata.id, msgMetadata.read, 'Message from ' + data.sender + ' [' + msgMetadata.timestamp + ']', data.body);
			},
			error: function(xhr, textStatus, errorThrown){
				bootbox.alert("Error loading message: " + xhr.status + ' ' + textStatus);
			},
			dataType: 'json'
		});
	}

	// post msg.read to server
	function markMsgAsRead(msgId){
		$.ajax({
			type: "POST",
			url: WEB_ROOT() + "/api/message/" + msgId + '/read',
			headers: getCsrfHeaders(),
			success: function(data){
				//
			},
			error: function(xhr, textStatus, errorThrown){
				//
			},
			dataType: 'json'
		});
	}

	// delete msg from server
	function deleteMsg(msgId, row){
		$.ajax({
			type: "DELETE",
			url: WEB_ROOT() + "/api/message/" + msgId,
			headers: getCsrfHeaders(),
			success: function(data){
				bootbox.alert("Message deleted!");
				row.hide();
			},
			error: function(xhr, textStatus, errorThrown){
				bootbox.alert("Error deleting message: " + xhr.status + ' ' + textStatus);
			},
			dataType: 'json'
		});
	}

	// populate and display message modal
	function drawModal(msgId, read, title, body){
		$('#myModalLabel').text(title);
		$('#msgModalBody').html('<pre>' + body + '</pre>');
		$('#msgModalMsgId').val(msgId);
		$('#msgModalIsRead').val(read);

		$('#msgModal').modal();
	}

	$( "#readMsg" ).click(function(e) {
		getMsg(getMsgMetadata(this));
		$(this).parent().parent();
	});

	$( "#closeMsg" ).click(function(e) {
		if( ! $('#msgModalIsRead').val() ){
			markMsgAsRead($('#msgModalMsgId').val());
		}

	});

	$( "#deleteMsg" ).click(function(e) {
		var row = $(this).parent().parent();
		var msgId = getMsgMetadata(this).id;	
		deleteMsg(msgId, row);
	});
});