<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Duty Management</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">Duty Management</h1>
		<div class="table-responsive">
			<p>
				<a href="${pageContext.request.contextPath}/admin/duties/new">
					<button type="button" class="btn btn-lg btn-success">Add Duty</button>
				</a>
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
						<tr>
							<td>${duty.id}</td>
							<td><c:out value="${duty.name}"/></td>
							<td><c:out value="${duty.description}"/></td>
							<td>${duty.sortOrder}</td>
							<td><a href="${pageContext.request.contextPath}/admin/duties/${duty.id}"><button type="button" class="btn btn-xs btn-primary">Edit</button></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
		$(document).ready(function() {
		    $('#dutyTable').DataTable({
		    	"order": [[3, "asc"]],
		    	stateSave: true
		    });
		} );
	</script>
</body>

</html>


