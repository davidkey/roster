<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Event Scheduling</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">Event Scheduling</h1>
		<div class="table-responsive">
			<table class="table table-striped" id="eventTypeTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Description</th>
						<th>Interval</th>
						<th># Duties</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="eventType" items="${eventTypes}">
						<tr>
							<td>${eventType.id}</td>
							<td><c:out value="${eventType.name}"/></td>
							<td><c:out value="${eventType.description}"/></td>
							<td>${eventType.interval} - ${eventType.intervalDetail}</td>
							<td>${fn:length(eventType.duties)}</td> <%--  does this work if duties is empty / null? --%>
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


