<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Rosters</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">Rosters &amp; Events</h1>
		<div class="table-responsive">
			<p>
				<a href="${pageContext.request.contextPath}/admin/rosters/generate">
					<button type="button" class="btn btn-lg btn-success">Generate Next Set</button>
				</a>
			</p>
			<br/>
			<table class="table table-striped" id="eventTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Date</th>
						<th>Name</th>
						<th>Generated?</th>
						<th>Complete?</th>
						<th>Approved?</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="event" items="${events}">
						<tr>
							<td>${event.id}</td>
							<td>${event.dateEvent}</td>
							<td><c:out value="${event.name}" /></td>
							<td>${event.rosterGenerated}</td>
							<td>${event.rosterFullyPopulated}</td>
							<td>${event.approved}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
		$(document).ready(function() {
		    $('#eventTable').DataTable({
		    	"order": [[1, "desc"]]
		    });
		} );
	</script>
</body>

</html>


