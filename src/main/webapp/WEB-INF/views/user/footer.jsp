<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- footer -->
<!--[if lt IE 9]>
	    <script type='text/javascript' src='//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js'></script>
	<![endif]-->
<!--[if gte IE 9]><!-->
<script src="<c:url value="/resources/jquery/jquery-2.1.4.min.js"/>"></script>
<!--<![endif]-->
<script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/js/bootbox.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/app/setActiveNav.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/app/logout.js"/>"></script>
<script>
$(document).ready(function() {
	function updateDutyCount(){
		$.get(WEB_ROOT() + "/user/upcomingDuties/count", function( data ) {
			$( "#upcomingDutiesCount" ).html( data );
		});
	}

	updateDutyCount();
});
</script>