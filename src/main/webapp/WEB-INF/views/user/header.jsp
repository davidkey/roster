<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<%-- <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags --> --%>
	<!-- build info: ${display_version} -->
	<sec:csrfMetaTags />
	<jsp:include page="../shared/favicon.jsp" />
	
	<!-- Bootstrap -->
	<link href="<c:url value="/resources/css/themes/bootstrap.css"/>" rel="stylesheet">
	<link href="<c:url value="/resources/css/themes/bootswatch.min.css"/>" rel="stylesheet">
	
	<link rel="stylesheet" type='text/css' href="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.css">
	
	<style>
	body {
		min-height: 2000px;
		padding-top: 70px;
	}
	</style>
	<script type="text/javascript">
	var WEB_ROOT = function(){
		return "${pageContext.request.contextPath}";
	}
	</script>