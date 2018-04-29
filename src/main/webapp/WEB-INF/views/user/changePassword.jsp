<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="header.jsp"/>
	<title>Roster.Guru - Change Password</title>
</head>
<body>

	<jsp:include page="nav.jsp"/>
	<div class="container">
		<jsp:include page="../shared/flashMessages.jsp" />
		<h1 class="page-header">Change Password</h1>
		<form:form modelAttribute="changePasswordForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			
			<div class="form-group">
				<label for="currentPassword">Current Password</label> 
				<form:password path="currentPassword" class="form-control" placeholder="" />
				<form:errors path="currentPassword" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="newPassword">New Password</label> 
				<form:password path="newPassword" class="form-control" placeholder="" />
				<form:errors path="newPassword" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="newPasswordConfirm">Reenter New Password</label> 
				<form:password path="newPasswordConfirm" class="form-control" placeholder="" />
				<form:errors path="newPasswordConfirm" class="alert-danger" />
			</div>

			<button type="submit" class="btn btn-success">Save</button>
		</form:form>
	</div>
	

	<jsp:include page="footer.jsp"/>
</body>
</html>
