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
						<th>Event Name</th>
						<th>Event Date</th>
						<th>Duty</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="duty" items="${upcomingDuties}">
						<tr>
							<td><c:out value="${duty.eventName}"/></td>
							<td>${duty.eventDate}</td>
							<td><c:out value="${duty.dutyName}"/></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

	</div>


	<jsp:include page="footer.jsp" />
</body>
</html>
