<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>

	<jsp:include page="./shared/header.jsp" />
	<link href="<c:url value="/resources/css/signin.css"/>" rel="stylesheet">
	
	<title>Password Reset</title>
</head>
<body>
 <div class="container">
	<form:form commandName="passwordResetForm" class="form-signin">  
		<c:if test="${error != null}">
			<div class="alert alert-danger" role="alert">
			  <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			  <span class="sr-only">Error:</span>
			  ${error}
			</div>
		</c:if>
		<c:if test="${success != null}">
			<div class="alert alert-success" role="alert">
			  <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
			  <span class="sr-only">Success:</span>
			  ${success}
			</div>
		</c:if>
		
		<c:if test="${invalidToken == null}">
		<div class="form-group">
			<label for="emailAddress">Email Address</label> 
			<form:input path="emailAddress" class="form-control" placeholder="johndoe@gmail.com" />
			<form:errors path="emailAddress" class="alert-danger" />
		</div>
		<div class="form-group">
			<label for="password">New Password</label> 
			<form:input type="password" path="password" class="form-control" />
			<form:errors path="password" class="alert-danger" />
		</div>
		<div class="form-group">
			<label for="confirmPassword">Confirm New Password</label> 
			<form:input type="password" path="confirmPassword" class="form-control" />
			<form:errors path="confirmPassword" class="alert-danger" />
		</div>
		<button type="submit" id="submit" class="btn btn-lg btn-success btn-block">Reset Password</button>
	  	</c:if>
	</form:form>

    </div> <!-- /container -->
</body>
</html>