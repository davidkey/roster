<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- flash messages (if applicable) --%>
<c:if test="${msg_error != null}">
	<div class="alert alert-danger">
		<a href="#" class="close" data-dismiss="alert">&times;</a>
		<c:out value="${msg_error}"/>
	</div>
</c:if>
<c:if test="${msg_success != null}">
	<div class="alert alert-success">
		<a href="#" class="close" data-dismiss="alert">&times;</a>
		<c:out value="${msg_success}"/>
	</div>
</c:if>
<c:if test="${msg_notice != null}">
	<div class="alert alert-notice">
		<a href="#" class="close" data-dismiss="alert">&times;</a>
		<c:out value="${msg_notice}"/>
	</div>
</c:if>