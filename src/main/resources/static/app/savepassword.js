$(document).ready(function() {

	function savePassword(){
		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");

		var headers = {};
		headers[csrfHeader] = csrfToken;

		var id = $('#id').val();
		var password = $('#password').val();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: 'json',
			url: WEB_ROOT() + "/api/person/password",
			headers: headers,
			data: JSON.stringify({'id': id, 'password': password}),
			success: function(data){
				if(data && data['response'] === 'OK'){
					bootbox.alert("Password updated!", function(){
						$( "#password" ).val('');
					});
				} else {
					bootbox.alert(data['response'] + ": " + data['detail']);
				}
			},
			error: function(xhr, textStatus, errorThrown){
				bootbox.alert("Error setting password: " + xhr.status + ' ' + textStatus);
			}
		});
	}


	$( "#setPassword" ).click(function(e, btn) {
		savePassword();
	});

	$( "#password" ).bind('keypress', function(e) {
		if(e.keyCode==13){
			savePassword();
		}
	});

});