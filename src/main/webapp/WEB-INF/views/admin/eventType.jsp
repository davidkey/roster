<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />

 <link rel="stylesheet" type='text/css' href="<c:url value="/resources/css/jquery.timepicker.css"/>">

<title>Event Type</title>
</head>

<body>
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">Event Type</h1>

		<form:form action="${pageContext.request.contextPath}/admin/eventTypes" commandName="eventType">
			<form:hidden path="id"/>			
			<div class="form-group">
				<label for="name">Name</label>
				<form:input path="name" class="form-control" placeholder="Sunday AM"/>
				<form:errors path="name" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="description">Description</label>
				<form:input path="description" class="form-control" placeholder="Sunday AM Worship Service"/>
				<form:errors path="description" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="startTime">Start Time</label>
				<form:input id="startTimePicker" path="startTime" class="form-control" value="${eventType.startTime}" />
				<form:errors path="startTime" class="alert-danger" />
			</div>
			
			<div class="form-group">
				<label for="endTime">End Time</label>
				<form:input id="endTimePicker" path="endTime" class="form-control" value="${eventType.endTime}"/>
				<form:errors path="endTime" class="alert-danger" />
			</div>
			
			<!-- end start time & end time -->
			
			<div class="form-group">
				<label for="interval">Interval</label>
				<form:select path="interval" id="intervalSelect" class="form-control">
					<form:option value="">&nbsp;</form:option>
					<form:options items="${eventTypeIntervals}" />
				</form:select>
				<form:errors path="interval" class="alert-danger" />
			</div>
			
			<c:if test="${not empty eventType.intervalDetail}">
			 	<div class="form-group" id="intervalDetailFormGroup">
					<label for="intervalDetail">Interval Detail</label>
					<form:input path="intervalDetail" id="intervalDetail" class="form-control" placeholder="Sunday"/>
					<form:errors path="intervalDetail" class="alert-danger" />
				</div>
			</c:if>
			
			<!-- duties -->			
			<div class="form-group">
				<label for="duties">Duties</label>
				<c:set var="count" value="0" scope="page" />
				<c:forEach items="${allPossibleDuties}" var="duty" varStatus="status">
					<c:if test="${eventType.duties ne null}">
						<s:eval expression=" T(java.util.Collections).frequency(eventType.duties, duty)" var="alreadySelectedTimes" />
					</c:if>
					<c:if test="${eventType.duties eq null}">
						<c:set var="alreadySelectedTimes" value="0" scope="page"/>
					</c:if>
					<c:choose>
						<c:when test="${alreadySelectedTimes ge 1}">
							<c:forEach begin="1" end="${alreadySelectedTimes}" var="val">
								<c:set var="count" value="${count + 1}" scope="page"/>
								<div class="checkbox">
									<label>
							    		<input class="dutyCheckbox" type="checkbox" name="duties[${count}].id" value="${duty.id}" checked="checked" /> <c:out value="${duty.name}" />
									</label>
									<c:if test="${val eq 1}">
										&nbsp;
										<button type="button" class="btn btn-default btn-xs add-another">Duplicate Me</button>
									</c:if>
								</div>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<c:set var="count" value="${count + 1}" scope="page"/>
							<div class="checkbox">
								<label>
									<input class="dutyCheckbox" type="checkbox" name="duties[${count}].id" value="${duty.id}" /> <c:out value="${duty.name}" />
								</label> 
								&nbsp;
								<button type="button" class="btn btn-default btn-xs add-another">Duplicate Me</button>
							</div>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
			
			<!-- end duties -->
			<c:if test="${not empty eventType.id && eventType.id gt 0}">
				<button type="button" class="btn btn-warning" id="deleteMe">Delete Event Type</button>
			</c:if>
			<button type="submit" class="btn btn-default">Save</button>
		</form:form>
		
		
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script src="<c:url value="/resources/js/jquery.timepicker.min.js"/>"></script>
	<script src="<c:url value="/resources/app/eventType.js"/>"></script>
</body>

</html>


