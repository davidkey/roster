<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

		<form:form action="${pageContext.request.contextPath}/admin/eventScheduling" commandName="eventType">
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
			
			<button type="submit" class="btn btn-default">Save</button>
		</form:form>
		
		
	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script>
	$(document).ready(function() {
		function createDetailNode(parentNode, interval){
			var existingDetailNode = $('#intervalDetailFormGroup');
			if(existingDetailNode){
				existingDetailNode.remove();
			}
			
			if(interval === 'DAILY'){
				return; // no need to do anything
			}
			
			var nodeHtml = '<div class="form-group" id="intervalDetailFormGroup">';
			switch(interval) {
				case 'WEEKLY':
					nodeHtml += '<label for="intervalDetail">Day of Week</label>';
					nodeHtml += '<select class="form-control" id="intervalDetail" name="intervalDetail">';
					nodeHtml += '	<option value="SUNDAY">Sunday</option>';
					nodeHtml += '	<option value="MONDAY">Monday</option>';
					nodeHtml += '	<option value="TUESDAY">Tuesday</option>';
					nodeHtml += '	<option value="WEDNESDAY">Wednesday</option>';
					nodeHtml += '	<option value="THURSDAY">Thursday</option>';
					nodeHtml += '	<option value="FRIDAY">Friday</option>';
					nodeHtml += '	<option value="SATURDAY">Saturday</option>';
					nodeHtml += '</select>';
					break;
				case 'MONTHLY':
					nodeHtml += '<label for="intervalDetail">Day of Month</label>';
					nodeHtml += '<select class="form-control" id="intervalDetail" name="intervalDetail">';
					for(var i = 1; i <= 31; i++){
						nodeHtml += '	<option value="' + i + '">' + i + '</option>';
					}
					nodeHtml += '</select>';
					break;
				case 'ONCE':
					nodeHtml += '<label for="intervalDetail">Date of Event</label>';
					nodeHtml += '<input class="form-control" id="intervalDetail" name="intervalDetail" type="text" placeholder="12/25/2015" />';
					break;
				default:
					throw 'Interval type not defined!';
					break;
			}
			
			nodeHtml += '</div>'
			
			$(nodeHtml).insertAfter($(parentNode).parent());
		}
		
		$( "#intervalSelect" ).change(function(val) {
			var selectedValue = $(this).find(":selected").attr('value');
			createDetailNode(this, selectedValue);
		});
	});
	</script>
</body>

</html>


