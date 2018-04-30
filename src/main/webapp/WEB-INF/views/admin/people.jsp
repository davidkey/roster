<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="duty" uri="/WEB-INF/duty.tld"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>People</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">People</h1>
		<div class="table-responsive">
			<p>
				<a href="${pageContext.request.contextPath}/admin/people/new" class="btn btn-lg btn-success">Add New Person</a>
			</p>
			<table class="table table-striped" id="personTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Last Name</th>
						<th>First Name</th>
						<th>Email</th>
						<th>Active?</th>
						<th>Updated</th>
						<th>Edit</th>
						<th>Manage Duties</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="person" items="${people}">
						<tr>
							<td>${person.id}</td>
							<td><c:out value="${person.nameLast}"/></td>
							<td><c:out value="${person.nameFirst}"/></td>
							<td><c:out value="${person.emailAddress}"/></td>
							<td>${person.active ? 'Yes' : 'No'}</td>
							<td><duty:formatDate value="${person.lastUpdated}" pattern="yyyy-MM-dd hh:mm a" /></td>
							<td><a href="${pageContext.request.contextPath}/admin/people/${person.id}" class="btn btn-xs btn-primary">Edit</a></td>
							<td><a href="${pageContext.request.contextPath}/admin/people/${person.id}/duties" class="btn btn-xs btn-info">Manage Duties</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
		$(document).ready(function() {
		    $('#personTable').DataTable({
		    	"order": [[1, "asc"]],
		    	stateSave: true
		    });
		} );
	</script>
</body>

</html>


