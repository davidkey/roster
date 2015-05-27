<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Duty</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />
	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">Duty</h1>
		<form:form action="${pageContext.request.contextPath}/admin/duties" commandName="duty">
			<form:hidden path="id"/>
			<div class="form-group">
				<label for="nameFirst">Name</label>
				<form:input path="name" class="form-control" placeholder="Scripture Reading"/>
				<form:errors path="name" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="description">Description</label>
				<form:input path="description" class="form-control" placeholder="Reads section of scripture to congregation"/>
				<form:errors path="description" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="sortOrder">Sort Order</label>
				<form:select path="sortOrder" class="form-control">
					<c:forEach begin="1" end="${maxSortOrder}" var="val">
						<form:option value="${val}">${val}</form:option>
					</c:forEach>

				</form:select>
				<form:errors path="sortOrder" class="alert-danger" />
			</div>

			<button type="submit" class="btn btn-default">Save</button>
		</form:form>
	</div>
	<jsp:include page="../shared/footer.jsp" />
</body>

</html>


