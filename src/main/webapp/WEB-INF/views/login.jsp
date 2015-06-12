<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>

	<jsp:include page="./shared/header.jsp" />
	<link href="<c:url value="/resources/css/signin.css"/>" rel="stylesheet">
	
	<title>Log In</title>
</head>
<body>
 <div class="container">
	<c:url value="/login" var="loginUrl"/>	
	<form action="${loginUrl}" method="post" class="form-signin">  
		<c:if test="${error != null}">
			<div class="error">${error}</div>
		</c:if>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<h2 class="form-signin-heading">Please sign in</h2>
		
		<label for="username" class="sr-only">Email address</label>
		<input type="email" id="username" name="username" class="form-control" placeholder="Email address" required autofocus>
		
		<label for="password" class="sr-only">Password</label>
		<input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
		 <%-- 
		 <div class="checkbox">
		 <label>
		    <input type="checkbox" value="remember-me"> Remember me
		  </label>
		  </div>
		  --%>
	  
	  <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
	</form>

    </div> <!-- /container -->
</body>
</html>