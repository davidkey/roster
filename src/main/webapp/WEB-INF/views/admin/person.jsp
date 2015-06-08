<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
<title>Add / Edit Person</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">Person</h1>

		<form:form action="${pageContext.request.contextPath}/admin/people" commandName="person">
			<form:hidden path="id"/>
			<div class="form-group">
				<label for="nameFirst">First Name</label>
				<form:input path="nameFirst" class="form-control" placeholder="John"/>
				<form:errors path="nameFirst" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="nameLast">Last Name</label>
				<form:input path="nameLast" class="form-control" placeholder="Doe"/>
				<form:errors path="nameLast" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="emailAddress">Email</label>
				<form:input path="emailAddress" class="form-control" placeholder="jdoe@example.com"/>
				<form:errors path="emailAddress" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label>
					<form:checkbox path="active"/> Is Active?
				</label>
			</div>
			
			<button type="submit" class="btn btn-default">Save</button>
		</form:form>
		
		<c:if test="${person.id gt 0}">
			<hr/>
			<div class="form-group">
					<label for="password">Set Password</label>
					<input id="password" type="password" class="form-control"/>
					<form:errors path="password" class="alert-danger" />
					<button id="setPassword" class="btn btn-default">Set Password</button> 
			</div>
		</c:if>
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
	$(document).ready(function() {
		$( "#setPassword" ).click(function(e, btn) {
			var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
			var csrfHeader = $("meta[name='_csrf_header']").attr("content");
			var csrfToken = $("meta[name='_csrf']").attr("content");
			
			var headers = {};
			headers[csrfHeader] = csrfToken;
			
			var id = $('#id').val();
			var password = $('#password').val();
			
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: 'json',
				url: WEB_ROOT() + "/api/person/password",
				headers: headers,
				data: JSON.stringify({'id': id, 'password': password}),
				success: function(data){
					if(data && data['response'] === 'OK'){
						bootbox.alert("Password updated!");
					} else {
						bootbox.alert("Error setting password!");
					}
				},
				error: function(xhr, textStatus, errorThrown){
					bootbox.alert("Error setting password: " + xhr.status + ' ' + textStatus);
				}
			});
		});
	});
	</script>
</body>

</html>


