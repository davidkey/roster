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

	// post msg.read(true) to server
	function markMsgAsRead(msgId, row){
		$.ajax({
			type: "POST",
			url: WEB_ROOT() + "/api/message/" + msgId + '/read',
			headers: getCsrfHeaders(),
			success: function(data){
				row.removeClass('warning');
				row.addClass('active');
				row.find('.msgIsRead').html("true");
				row.find('.changeReadStatus').text('Mark Unread');
			},
			error: function(xhr, textStatus, errorThrown){
				//
			},
			dataType: 'json'
		});
	}

	// post msg.read(false) to server
	function markMsgAsUnread(msgId, row){
		$.ajax({
			type: "POST",
			url: WEB_ROOT() + "/api/message/" + msgId + '/unread',
			headers: getCsrfHeaders(),
			success: function(data){
				row.removeClass('active');
				row.addClass('warning');
				row.find('.msgIsRead').html("false");
				row.find('.changeReadStatus').text('Mark Read');
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

	$( ".readMsg" ).click(function(e) {
		getMsg(getMsgMetadata(this));
		$(this).parent().parent();
	});

	function findRowById(msgId){
		var row = null;
		$('.msgId').each(function(){
			if($(this).text() === msgId){
				row = $(this).parent();
				return false;
			}
		});
		
		return row;
	}

	$( "#closeMsg" ).click(function(e) {
		if( $('#msgModalIsRead').val() === 'false' ){
			var msgId = $('#msgModalMsgId').val();
			var row = findRowById(msgId);
			markMsgAsRead(msgId, row);
		}

	});

	$( ".changeReadStatus" ).click(function(e){
		var row = $(this).parent().parent();
		var msg = getMsgMetadata(this);
		if(msg.read === 'true'){
			markMsgAsUnread(msg.id, row);
		} else {
			markMsgAsRead(msg.id, row);
		}
	});

	$( ".deleteMsg" ).click(function(e) {
		var row = $(this).parent().parent();
		var msgId = getMsgMetadata(this).id;	

		bootbox.confirm("Are you sure you want to delete this message?", function(result) {
			if(result){
				deleteMsg(msgId, row);
			}
		}); 
	});
});