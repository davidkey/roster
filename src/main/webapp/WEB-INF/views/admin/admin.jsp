<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <jsp:include page="../shared/header.jsp" />
    
    <link rel="stylesheet" type='text/css' href="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.css">
    <title>Roster.Guru - Admin</title>
    <style> #calendar { width: 900px; } </style>
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
     <script type='text/javascript' src="<c:url value="/resources/app/eventCalendar.js"/>"></script>
  </body>
  
</html>


