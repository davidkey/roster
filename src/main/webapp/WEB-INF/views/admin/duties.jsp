<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Duty Management</title>
<style>
.ui-sortable tr {     cursor:pointer; }    
.ui-sortable tr:hover {     background:rgba(244,251,17,0.45); } 
</style>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">Duty Management</h1>
		<div class="table-responsive">
			<p>
				<a href="${pageContext.request.contextPath}/admin/duties/new" class="btn btn-lg btn-success">Add Duty</a>
			</p>
			<table class="table table-striped" id="dutyTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Description</th>
						<th>Sort Order</th>
						<th>Edit</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="duty" items="${duties}">
						<tr class="dutyRow">
							<td class="dutyId">${duty.id}</td>
							<td><c:out value="${duty.name}"/></td>
							<td><c:out value="${duty.description}"/></td>
							<td class="sortOrder">${duty.sortOrder}</td>
							<td><a href="${pageContext.request.contextPath}/admin/duties/${duty.id}" class="btn btn-xs btn-primary">Edit</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script src="<c:url value="/resources/jquery-ui/jquery-ui.min.js"/>"></script>
	<script>
	$(document).ready(function() {
	    //Helper function to keep table row from collapsing when being sorted
	    var fixHelperModified = function(e, tr) {
	        var $originals = tr.children();
	        var $helper = tr.clone();
	        $helper.children().each(function(index)
	        {
	          $(this).width($originals.eq(index).width())
	        });
	        return $helper;
	    };

	    //Make diagnosis table sortable
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
		console.log('updateServiceWithNewSorts');
		var sortArray = [];
		
		 $(tableID + " tr.dutyRow").each(function(index, obj) {
			var id = parseInt($(obj).find('.dutyId').html());
			sortArray[id] = index + 1;
		 });
		 
		 console.log(sortArray);
		 
		 
	}
	
	</script>
</body>

</html>


