     $(document).ready(function() {
    	 
		 var events = (function () {
			    var json = null;
			    $.ajax({
			        'async': true,
			        'global': false,
			        'url': WEB_ROOT() + "/admin/events/all/json",
			        'dataType': "json",
			        'success': function (data) {
			        	try {
				        	data.forEach(function(entry){
				        		entry['url'] = WEB_ROOT() + '/admin/rosters/' + entry['id'];
				        	});
			        	} catch(err) {
			        		// some browsers don't support .forEach yet...
			        	}
			        	populateCalendar(data);
			        }
			    });
			    return json;
			})(); 
 		
		function populateCalendar(data){
	 		$('#calendar').fullCalendar({
	 			header: {
	 				left: 'prev,next today',
	 				center: 'title',
	 				right: 'month,agendaWeek,agendaDay'
	 			},
	 			defaultDate: moment().format("YYYY-MM-DD"),
	 			defaultView: 'month',
	 			editable: false,
	 			events: data
	 		});
		}
 		
 	});