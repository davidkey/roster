<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
          <form class="navbar-form navbar-right">
            <input type="text" class="form-control" placeholder="Search...">
          </form>
        </div>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <div id="navsidebar" class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li class="active" id="navAdmin"><a href="<c:url value="/admin/"/>">Overview <span class="sr-only">(current)</span></a></li>
            <li id="navRoster"><a href="#">Rosters</a></li>
            <li id="navPeople"><a href="<c:url value="/admin/people/"/>">People</a></li>
            <li id="navEventScheduling"><a href="#">Event Scheduling</a></li>
            <li id="navDutyManagement"><a href="#">Duty Management</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li id="navSettings"><a href="">Settings</a></li>
            <li id="navAbout"><a href="">About</a></li>
          </ul>
        </div>