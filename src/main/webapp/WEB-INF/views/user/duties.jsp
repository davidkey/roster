<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="header.jsp" />
<title>Duty Roster - Duties</title>
<style>
#calendar {
	width: 900px;
}
</style>
</head>
<body>

	<jsp:include page="nav.jsp" />
	<div class="container">

		<div class="table-responsive">
			<table class="table table-striped" id="personTable">
				<thead>
					<tr>
						<th style="display:none;">id</th>
						<th>Event Name</th>
						<th>Event Date</th>
						<th>Duty</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="duty" items="${upcomingDuties}">
						<tr>
							<td style="display:none;" class="dutyId">${duty.dutyId}</td>
							<td style="display:none;" class="eventId">${duty.eventId}</td>
							<td><c:out value="${duty.eventName}"/></td>
							<td>${duty.eventDate}</td>
							<td><c:out value="${duty.dutyName}"/></td>
							<td><button class="btn btn-sm btn-warning optOut">Opt Out</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

	</div>


	<jsp:include page="footer.jsp" />
	<script>
$(document).ready(function() {
	function getDutyMetadata(row){
		var metadata = {};
		
		metadata.dutyId  = parseInt(row.find('.dutyId').html());
		metadata.eventId = parseInt(row.find('.eventId').html());
		
		return metadata;
	}
	
	function optOut(metadata, row){
		var headers = getCsrfHeaders();
		
		$.ajax({
			type: "POST",
			url: WEB_ROOT() + '/user/upcomingDuties/optOut',
			headers: getCsrfHeaders(),
			data: metadata,
			success: function(data){
				bootbox.alert('Successfully opted out of duty');
				$(row).hide();
				updateDutyCount();
			},
			error: function(xhr, textStatus, errorThrown){
				bootbox.alert("Error: " + xhr.status + ' ' + textStatus);
			}
		});
	}
	
	$(".optOut").click(function(e, btn){
		var row = $(this).parent().parent();
		var metadata = getDutyMetadata(row);
		
		console.log('metadata: ' + metadata);
		
		optOut(metadata, row);
	});
});
	</script>
</body>
</html>
