<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="header.jsp"/>
	<title>Duty Roster - Home</title>
	<style>
	#calendar {
		width: 900px;
	}
	</style>
</head>
<body>

	<jsp:include page="nav.jsp"/>
	<div class="container">

		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
			<h1>Navbar example</h1>
			<p>This example is a quick exercise to illustrate how the default, static and fixed to top navbar work. It includes the responsive CSS and HTML, so it also adapts to your viewport and device.</p>
			<p>To see the difference between static and fixed top navbars, just scroll.</p>
			<p>
				<a class="btn btn-lg btn-primary" href="../../components/#navbar" role="button">View navbar docs &raquo;</a>
			</p>
		</div>

	</div>
	

	<jsp:include page="footer.jsp"/>
</body>
</html>
