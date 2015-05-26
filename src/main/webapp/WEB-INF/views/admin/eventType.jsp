<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />
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

			<button type="submit" class="btn btn-default">Save</button>
		</form:form>
		
		
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
	$(document).ready(function() {
		
		/**
			Methods for handling interval detail
		**/
		function capitalizeFirstLetter(string) {
		    return string.charAt(0).toUpperCase() + string.slice(1);
		}
		
		function createDetailNode(parentNode, interval, value){
			var existingDetailNode = $('#intervalDetailFormGroup');
			if(existingDetailNode){
				existingDetailNode.remove();
			}
			
			if(interval === 'DAILY'){
				return; // no need to do anything
			}
			
			var daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
			
			var nodeHtml = '<div class="form-group" id="intervalDetailFormGroup">';
			switch(interval) {
				case 'WEEKLY':
					nodeHtml += '<label for="intervalDetail">Day of Week</label>';
					nodeHtml += '<select class="form-control" id="intervalDetail" name="intervalDetail">';
					for(var i = 0, len = daysOfWeek.length; i < len; i++ ){
						var day = daysOfWeek[i];
						if(value && value === day){
							nodeHtml += '	<option value="' + day +  '" selected="selected">' + capitalizeFirstLetter(day.toLowerCase()) + '</option>';
						} else {
							nodeHtml += '	<option value="' + day +  '">' + capitalizeFirstLetter(day.toLowerCase()) + '</option>';
						}
					}
					nodeHtml += '</select>';
					break;
				case 'MONTHLY':
					nodeHtml += '<label for="intervalDetail">Day of Month</label>';
					nodeHtml += '<select class="form-control" id="intervalDetail" name="intervalDetail">';
					for(var i = 1; i <= 31; i++){
						if(value && value == i){
							nodeHtml += '	<option value="' + i + '" selected="selected">' + i + '</option>';
						} else {
							nodeHtml += '	<option value="' + i + '">' + i + '</option>';
						}
					}
					nodeHtml += '</select>';
					break;
				case 'ONCE':
					nodeHtml += '<label for="intervalDetail">Date of Event</label>';
					nodeHtml += '<input class="form-control" id="intervalDetail" name="intervalDetail" type="text" placeholder="12/25/2015"';
					if(value){
						nodeHtml += ' value="' + value + '" '
					}
					nodeHtml += '/>';
					break;
				default:
					//throw 'Interval type not defined!';
					break;
			}
			
			nodeHtml += '</div>'
			
			$(nodeHtml).insertAfter($(parentNode).parent());
		}
		
		$( "#intervalSelect" ).change(function(val) {
			var selectedValue = $(this).find(":selected").attr('value');
			createDetailNode(this, selectedValue);
		});
		
		var selected = $( "#intervalSelect" ).find(":selected");
		var selection = selected ? selected.attr('value') : '';
		if(selection){
			var value = $('#intervalDetail').attr('value');
			createDetailNode(selected.parent(), selection, value);
		}
		
		/**
			Methods for duty listing and duplication
		**/
		$( ".add-another" ).click(function(e, btn) {
			  //alert( "Handler for .click() called." );
			  var count = $('.dutyCheckbox').size() + 1;
			  var parent = $(this).parent();
			  var clonedParent = parent.clone();
			  var inputName = clonedParent.find('input').attr('name');
			  clonedParent.find('input').attr('name', inputName.replace(/\[.+\]/, "[" + count + "]"));
			  clonedParent.find('button').remove()

			  clonedParent.insertAfter(parent);
		});
		
	});
	</script>
</body>

</html>


