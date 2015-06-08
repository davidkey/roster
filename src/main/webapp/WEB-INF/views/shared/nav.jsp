<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#" title="${display_version}">Duty Roster</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
        	<sec:authorize access="isAuthenticated()"> 
	          <ul class="nav navbar-nav navbar-right">
	            <li><a href="#"><sec:authentication property="principal.username" /></a></li>
	            <sec:authorize url="/user">
					<li><a href="<c:url value="/user"/>">Exit Admin</a></li>
				</sec:authorize>
	            <li><a id="logoutLink" href="#">Log Out</a>
	          </ul>
	        </sec:authorize>
	        <sec:authorize access="isAnonymous()"> 
	          <ul class="nav navbar-nav navbar-right">
	            <li><a href="<c:url value="j_spring_security_logout" />">Log In</a></li>
	          </ul>
	        </sec:authorize>
	        
          <form class="navbar-form navbar-right">
            <input type="text" id="autocomplete" class="form-control" placeholder="Search...">
          </form>
        </div>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <div id="navsidebar" class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li id="navAdmin"><a href="<c:url value="/admin/"/>">Overview <span class="sr-only">(current)</span></a></li>
            <li id="navRosters"><a href="<c:url value="/admin/rosters/"/>">Rosters</a></li>
            <li id="navPeople"><a href="<c:url value="/admin/people/"/>">People</a></li>
            <li id="navEventTypes"><a href="<c:url value="/admin/eventTypes/"/>">Event Scheduling</a></li>
            <li id="navDuties"><a href="<c:url value="/admin/duties/"/>">Duty Management</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li id="navSettings"><a href="<c:url value="/admin/settings/"/>">Settings</a></li>
            <li id="navAbout"><a href="<c:url value="/admin/about/"/>">About</a></li>
          </ul>
        </div>
        