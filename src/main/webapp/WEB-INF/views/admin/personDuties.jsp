<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap-slider.css"/>

<title>Manage Duties</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">Manage Duties for <c:out value="${personName}"/></h1>

		<form method="POST">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<c:forEach var="duty" items="${duties}">
				<s:eval expression="person.getPreferenceForDuty(duty)" var="currentPreferenceRanking" />
				<div class="form-group">
					<label for="duty_${duty.id}_slider"><c:out value="${duty.name}"/></label>
					<br/>
					<input type="text" style="width:50%;" id="duty_${duty.id}_slider" name="duty_${duty.id}_slider" data-slider-id='duty_${duty.id}_slider' class="sliderz"
						data-slider-min="-1" data-slider-max="9" data-slider-step="1" data-slider-value="${currentPreferenceRanking}"/>
				</div>
				<br/>
			</c:forEach>
			<button type="submit" class="btn btn-success">Save</button>
		</form>
		
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap-slider.js"></script>
	<script>
		$.each($('.sliderz'), function(index, obj){
			$(obj).slider({
				tooltip: 'hide',
				ticks: [-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
			    ticks_labels: ['Never', '', 'Very Rarely', '', 'Rarely', '', 'Sometimes', '', 'Often', '', 'Very Often']
			});
		});
	</script>
</body>

</html>


