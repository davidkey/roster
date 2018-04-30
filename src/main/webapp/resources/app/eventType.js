$(document).ready(function() {
		
		function addZero(i) {
		    if (i < 10) {
		        i = "0" + i;
		    }
		    return i;
		}
		
		function getTimeFromField(field){
			/** FIXME: this is hideous **/
			var dateStr = field.attr('value');
			
			var d = new Date(dateStr);
			if(d && d != 'Invalid Date'){
				var hours = addZero(d.getHours());
				var mins = addZero(d.getMinutes());
				
				dateStr = hours + ":" + mins + ":00";
			}

			if(dateStr.length === 8){
				var hour = dateStr.split(':')[0];
				if(hour > 12){
					hour -= 12;
					dateStr = hour + ":" + dateStr.split(':')[1] + ' AM';
				} else {
					if(hour == 0){
						hour = 12;
					}
					dateStr = hour + ":" + dateStr.split(':')[1] + ' PM';
				}
			}

			return dateStr;
		}
		
		/**
			setting up timepickers for start / end times
		**/
		
		$('#startTimePicker').timepicker({
			'step': 15,
			'timeFormat': 'h:i A'
		});
		$('#endTimePicker').timepicker({
			'step': 15,
			'timeFormat': 'h:i A'
		});
		
		var initStartTime = getTimeFromField($('#startTimePicker'));
		var initEndTime = getTimeFromField($('#endTimePicker'));
		
		$('#startTimePicker').timepicker('setTime', initStartTime);
		$('#endTimePicker').timepicker('setTime', initEndTime);
		
		$('#startTimePicker').on('changeTime', function() {
		    $('#endTimePicker').timepicker('option', {
			    	'minTime': $('#startTimePicker').val(),
			    	'maxTime': '11:45pm',
			    	'showDuration': true
		    	}
		    );
		});
		
		/**
			Methods for handling interval detail
		**/
		function capitalizeFirstLetter(string) {
		    return string.charAt(0).toUpperCase() + string.slice(1);
		}
		
		function createDetailNode(parentNode, interval, value){
			var existingDetailNode = $('#intervalDetailFormGroup');
			if(existingDetailNode){
				existingDetailNode.remove();
			}
			
			if(interval === 'DAILY'){
				return; // no need to do anything
			}
			
			var daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
			
			var nodeHtml = '<div class="form-group" id="intervalDetailFormGroup">';
			switch(interval) {
				case 'WEEKLY':
					nodeHtml += '<label for="intervalDetail">Day of Week</label>';
					nodeHtml += '<select class="form-control" id="intervalDetail" name="intervalDetail">';
					for(var i = 0, len = daysOfWeek.length; i < len; i++ ){
						var day = daysOfWeek[i];
						if(value && value === day){
							nodeHtml += '	<option value="' + day +  '" selected="selected">' + capitalizeFirstLetter(day.toLowerCase()) + '</option>';
						} else {
							nodeHtml += '	<option value="' + day +  '">' + capitalizeFirstLetter(day.toLowerCase()) + '</option>';
						}
					}
					nodeHtml += '</select>';
					break;
				case 'MONTHLY':
					nodeHtml += '<label for="intervalDetail">Day of Month</label>';
					nodeHtml += '<select class="form-control" id="intervalDetail" name="intervalDetail">';
					for(var i = 1; i <= 31; i++){
						if(value && value == i){
							nodeHtml += '	<option value="' + i + '" selected="selected">' + i + '</option>';
						} else {
							nodeHtml += '	<option value="' + i + '">' + i + '</option>';
						}
					}
					nodeHtml += '</select>';
					break;
				case 'ONCE':
					nodeHtml += '<label for="intervalDetail">Date of Event</label>';
					nodeHtml += '<input class="form-control" id="intervalDetail" name="intervalDetail" type="text" placeholder="12/25/2015"';
					if(value){
						nodeHtml += ' value="' + value + '" '
					}
					nodeHtml += '/>';
					break;
				default:
					//throw 'Interval type not defined!';
					break;
			}
			
			nodeHtml += '</div>'
			
			$(nodeHtml).insertAfter($(parentNode).parent());
		}
		
		$( "#intervalSelect" ).change(function(val) {
			var selectedValue = $(this).find(":selected").attr('value');
			createDetailNode(this, selectedValue);
		});
		
		var selected = $( "#intervalSelect" ).find(":selected");
		var selection = selected ? selected.attr('value') : '';
		if(selection){
			var value = $('#intervalDetail').attr('value');
			createDetailNode(selected.parent(), selection, value);
		}
		
		/**
			Methods for duty listing and duplication
		**/
		$( ".add-another" ).click(function(e, btn) {
			  //alert( "Handler for .click() called." );
			  var count = $('.dutyCheckbox').size() + 1;
			  var parent = $(this).parent();
			  var clonedParent = parent.clone();
			  var inputName = clonedParent.find('input').attr('name');
			  clonedParent.find('input').attr('name', inputName.replace(/\[.+\]/, "[" + count + "]"));
			  clonedParent.find('button').remove()

			  clonedParent.insertAfter(parent);
		});
		
		/**
			Delete logic - todo: clean this up - hideous
		**/
		$("#deleteMe").click(function(e, btn){
			bootbox.confirm("Are you sure you want to delete this event type?", function(result) {
				if(result){
					var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
					var csrfHeader = $("meta[name='_csrf_header']").attr("content");
					var csrfToken = $("meta[name='_csrf']").attr("content");
					
					var headers = {};
					headers[csrfHeader] = csrfToken;
					
					var id = $('#id').val();
					$.ajax({
						type: "DELETE",
						contentType: "application/json",
						dataType: 'json',
						url: WEB_ROOT() + "/api/eventType",
						headers: headers,
						data: JSON.stringify({'id': id}),
						success: function(data){
							if(data && data['response'] === 'OK'){
								bootbox.alert("Deleted successfully!", function() {
									window.location.href = WEB_ROOT() + "/admin/eventTypes/";
								});
							} else {
								bootbox.alert("Delete failed!");
							}
						},
						error: function(xhr, textStatus, errorThrown){
							bootbox.alert("Error deleting event type: " + xhr.status + ' ' + textStatus);
						}
					});
				}
			}); 
		});
		
	});