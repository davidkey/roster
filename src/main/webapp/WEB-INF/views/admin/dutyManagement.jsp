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
		<h1 class="page-header">Duty Management</h1>
		<div class="table-responsive">
			<table class="table table-striped" id="dutyTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Description</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="duty" items="${duties}">
						<tr>
							<td>${duty.id}</td>
							<td><c:out value="${duty.name}"/></td>
							<td><c:out value="${duty.description}"/></td>
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
		    	"order": [[0, "desc"]],
		    	stateSave: true
		    });
		} );
	</script>
</body>

</html>


