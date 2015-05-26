<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Event Types</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">Event Types</h1>
		<div class="table-responsive">
			<p>
				<a href="${pageContext.request.contextPath}/admin/eventTypes/new">
					<button type="button" class="btn btn-lg btn-success">Add Event</button>
				</a>
			</p>
			<table class="table table-striped" id="eventTypeTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Description</th>
						<th>Interval</th>
						<th># Duties</th>
						<th>Edit</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="eventType" items="${eventTypes}">
						<tr>
							<td>${eventType.id}</td>
							<td><c:out value="${eventType.name}"/></td>
							<td><c:out value="${eventType.description}"/></td>
							<td>${eventType.interval} - ${eventType.intervalDetail}</td>
							<td>${fn:length(eventType.duties)}</td>
							<td><a href="${pageContext.request.contextPath}/admin/eventTypes/${eventType.id}"><button type="button" class="btn btn-xs btn-primary">Edit</button></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
		$(document).ready(function() {
		    $('#eventTypeTable').DataTable({
		    	stateSave: true
		    });
		} );
	</script>
</body>

</html>


