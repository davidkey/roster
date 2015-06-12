<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Setup</title>
</head>

<body>

	<!-- Begin page content -->
	<div class="container">
		<div class="page-header">
			<h1>Welcome to Duty Roster!</h1>
		</div>
		<p class="lead">Just a couple of housekeeping items and then you can get started.</p>
		<form:form commandName="setupForm">
			<div class="form-group">
				<label for="emailAddress">Admin Email Address</label> 
				<form:input path="emailAddress" class="form-control" placeholder="johndoe@gmail.com" />
				<form:errors path="emailAddress" class="alert-danger" />
			</div>
			<div class="form-group">
				<label for="nameFirst">First Name</label> 
				<form:input path="nameFirst" class="form-control" placeholder="John" />
				<form:errors path="nameFirst" class="alert-danger" />
			</div>
			<div class="form-group">
				<label for="nameLast">Last Name</label> 
				<form:input path="nameLast" class="form-control" placeholder="Doe" />
				<form:errors path="nameLast" class="alert-danger" />
			</div>
			<div class="form-group">
				<label for="password">Password</label> 
				<form:input type="password" path="password" class="form-control" />
				<form:errors path="password" class="alert-danger" />
			</div>
			<div class="form-group">
				<label for="confirmPassword">Confirm Password</label> 
				<form:input type="password" path="confirmPassword" class="form-control" />
				<form:errors path="confirmPassword" class="alert-danger" />
			</div>
			<button type="submit" id="submit" class="btn btn-lg btn-success btn-block">Get Started</button>
		</form:form>
		
	</div>

	<!--[if lt IE 9]>
	    <script type='text/javascript' src='//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js'></script>
	<![endif]-->
	<!--[if gte IE 9]><!-->
    <script src="<c:url value="/resources/jquery/jquery-2.1.4.min.js"/>"></script>
	<!--<![endif]-->
    <script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js"/>"></script>
    <script src="<c:url value="/resources/js/bootbox.min.js"/>"></script>
</body>

</html>