<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>View Roster</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">View Roster</h1>

		<div id="roster">
			<h2><c:out value="${event.eventType.name}"/></h2>
			<h3>
				<fmt:formatDate pattern="MM/dd/yyyy" value="${event.dateEvent}" /> 
				<small><fmt:formatDate pattern="EEEE" value="${event.dateEvent}" /></small>
			</h3>
			
			<table class="table table-striped" id="rosterTable">
				<thead>
					<tr>
						<th>Duty</th>
						<th>Person</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="rosterItem" items="${roster}">
					<tr>
						<td><c:out value="${rosterItem.duty.name}"/></td>
						<td><c:out value="${rosterItem.person.nameFirst} ${rosterItem.person.nameLast}"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>

		</div>
		
		
	</div>

	<jsp:include page="../shared/footer.jsp" />
</body>

</html>


