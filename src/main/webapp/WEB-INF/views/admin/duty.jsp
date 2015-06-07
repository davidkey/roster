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
			
			<c:if test="${not empty duty.id && duty.id gt 0}">
				<button type="button" class="btn btn-warning" id="deleteMe">Delete Duty</button>
			</c:if>
			<button type="submit" class="btn btn-default">Save</button>
		</form:form>
	</div>
	<jsp:include page="../shared/footer.jsp" />
	<script>
		/**
		Delete logic - todo: clean this up - hideous
		**/
		$("#deleteMe").click(function(e, btn){
			bootbox.confirm("Are you sure you want to delete this event type?", function(result) {
				if(result){
					var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
					var csrfHeader = $("meta[name='_csrf_header']").attr("content");
					var csrfToken = $("meta[name='_csrf']").attr("content");
					
					var headers = {};
					headers[csrfHeader] = csrfToken;
					
					var id = $('#id').val();
					$.ajax({
						type: "DELETE",
						contentType: "application/json",
						dataType: 'json',
						url: WEB_ROOT() + "/api/duty",
						headers: headers,
						data: JSON.stringify({'id': id}),
						success: function(data){
							if(data && data['response'] === 'OK'){
								bootbox.alert("Deleted successfully!", function() {
									window.location.href = WEB_ROOT() + "/admin/duties/";
								});
							} else {
								bootbox.alert("Delete failed!");
							}
						}
					});
				}
			}); 
		});
	</script>
</body>

</html>


