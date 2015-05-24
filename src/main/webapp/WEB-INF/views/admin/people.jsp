<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>People</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">People</h1>
		<div class="table-responsive">
			<table class="table table-striped" id="personTable">
				<thead>
					<tr>
						<th>#</th>
						<th>Last Name</th>
						<th>First Name</th>
						<th>Email</th>
						<th>Active?</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="person" items="${people}">
						<tr>
							<td>${person.id}</td>
							<td>${person.nameLast}</td>
							<td>${person.nameFirst}</td>
							<td>${person.emailAddress}</td>
							<td>${person.active}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
		$(document).ready(function() {
		    $('#personTable').DataTable();
		} );
	</script>
</body>

</html>


