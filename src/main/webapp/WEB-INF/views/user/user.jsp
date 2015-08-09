<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="header.jsp"/>
	<title>Roster.Guru - Home</title>
</head>
<body>

	<jsp:include page="nav.jsp"/>
	<div class="container">
		<jsp:include page="../shared/flashMessages.jsp" />
		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
			<h1><c:out value="${personName}"/>,</h1>
			<p>
				Welcome to Roster Guru!
			</p>
			<p>
				Take a look at the navigation bar above to see what your options are.
			</p>
			<p>
				<a class="btn btn-lg btn-primary" href="<c:url value="/user/upcomingDuties"/>" role="button">View upcoming duties &raquo;</a>
			</p>
		</div>

	</div>
	

	<jsp:include page="footer.jsp"/>
</body>
</html>
