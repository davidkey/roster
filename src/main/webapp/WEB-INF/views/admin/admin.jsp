<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <jsp:include page="../shared/header.jsp" />
    
    <link rel="stylesheet" type='text/css' href="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.css">
    <!-- <link rel="stylesheet" type='text/css' href="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.print.css">  -->
    
    <title>Admin</title>
    
    <style>
	    #calendar {
			width: 900px;
			
		}
    </style>
    
  </head>
  
  <body>
    <jsp:include page="../shared/nav.jsp" />
        
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Dashboard</h1>
         
          <div id='calendar'></div>
        </div>
        
     <jsp:include page="../shared/footer.jsp" />
     <script type='text/javascript' src='//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js'></script>
     <script type='text/javascript' src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.js"></script>
     <script>
     $(document).ready(function() {
    	 
		 var events = (function () {
			    var json = null;
			    $.ajax({
			        'async': true,
			        'global': false,
			        'url': "${pageContext.request.contextPath}/admin/events/all/json",
			        'dataType': "json",
			        'success': function (data) {
			        	try {
				        	data.forEach(function(entry){
				        		entry['url'] = '${pageContext.request.contextPath}/admin/rosters/' + entry['id'];
				        	});
			        	} catch(err) {
			        		// some browsers don't support .forEach yet...
			        	}
			        	populateCalendar(data);
			        }
			    });
			    return json;
			})(); 
 		
		function populateCalendar(data){
	 		$('#calendar').fullCalendar({
	 			header: {
	 				left: 'prev,next today',
	 				center: 'title',
	 				right: 'month,agendaWeek,agendaDay'
	 			},
	 			defaultDate: moment().format("YYYY-MM-DD"),
	 			defaultView: 'month',
	 			editable: false,
	 			events: data
	 		});
		}
 		
 	});
     </script>
  </body>
  
</html>


