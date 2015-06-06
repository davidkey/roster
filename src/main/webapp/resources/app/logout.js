$(document).ready(function() {
    	$( "#logoutLink" ).click(function(e, btn) {
			 doLogout();
		});
    	
    	
    	function doLogout(){
    		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    		var csrfToken = $("meta[name='_csrf']").attr("content");
    		
    		var headers = {};
    		headers[csrfHeader] = csrfToken;
    		
    		$.ajax({
				type: "POST",
				url: WEB_ROOT() + "/logout",
				headers: headers,
				//data: JSON.stringify(sortArray),
				success: function(data, textStatus, xhr){
					window.location.href = WEB_ROOT();
				},
				error: function(xhr, textStatus, errorThrown){
					bootbox.alert("Error logging out: " + xhr.status + ' ' + textStatus);
				}
			});
    	}
    })