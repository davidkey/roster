$(document).ready(function() {
	    var fixHelperModified = function(e, tr) {
	        var $originals = tr.children();
	        var $helper = tr.clone();
	        $helper.children().each(function(index)
	        {
	          $(this).width($originals.eq(index).width())
	        });
	        return $helper;
	    };

	    $("#dutyTable tbody").sortable({
	        helper: fixHelperModified,
	        stop: function(event,ui) {
	        	renumber_table('#dutyTable');
	        	updateServiceWithNewSorts('#dutyTable');	
	        }
	    }).disableSelection();

	    /*
	    //Delete button in table rows
	    $('table').on('click','.btn-delete',function() {
	        tableID = '#' + $(this).closest('table').attr('id');
	        r = confirm('Delete this item?');
	        if(r) {
	            $(this).closest('tr').remove();
	            renumber_table(tableID);
	            }
	    });
	    */
	});

	//Renumber table rows
	function renumber_table(tableID) {
	    $(tableID + " tr").each(function() {
	        count = $(this).parent().children().index($(this)) + 1;
	        $(this).find('.sortOrder').html(count);
	    });
	}
	
	function updateServiceWithNewSorts(tableID){
		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");
		
		var headers = {};
		headers[csrfHeader] = csrfToken;
		
		var sortArray = [];
		
		 $(tableID + " tr.dutyRow").each(function(index, obj) {
			var id = (parseInt($(obj).find('.dutyId').html()));
			sortArray[index] = {'id': id, 'sortOrder' : (index + 1)};
		 });
		 
		 $.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: 'json',
				url: WEB_ROOT() + "/api/duty/sortOrder",
				headers: headers,
				data: JSON.stringify(sortArray),
				success: function(data, textStatus, xhr){
					if(xhr.status === 200 && data && data['response'] === 'OK'){
						//console.log('order saved OK');
					} else {
						bootbox.alert("Error updating order: " + xhr.status + ' ' + data['detail']);
					}
				},
				error: function(xhr, textStatus, errorThrown){
					bootbox.alert("Error updating order: " + xhr.status + ' ' + textStatus);
				}
			});
	}