<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Duty Management</title>
<style>
.ui-sortable tr {     cursor:pointer; }    
.ui-sortable tr:hover {     background:rgba(244,251,17,0.45) !important; } 
</style>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">Duty Management</h1>
		<div class="table-responsive">
			<p>
				<a href="${pageContext.request.contextPath}/admin/duties/new" class="btn btn-lg btn-success">Add Duty</a>
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
						<tr class="dutyRow">
							<td class="dutyId">${duty.id}</td>
							<td><c:out value="${duty.name}"/></td>
							<td><c:out value="${duty.description}"/></td>
							<td class="sortOrder">${duty.sortOrder}</td>
							<td><a href="${pageContext.request.contextPath}/admin/duties/${duty.id}" class="btn btn-xs btn-primary">Edit</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script src="<c:url value="/resources/jquery-ui/jquery-ui.min.js"/>"></script>
	<script src="<c:url value="/resources/app/dutyDraggr.js"/>"></script>
</body>

</html>


