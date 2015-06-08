<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!-- Fixed navbar -->
<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">Duty Roster</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="<c:url value="/user/"/>">Home</a></li>
				<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
					Upcoming Duties <span id="upcomingDutiesCount" class="label label-primary"></span> <span class="caret"></span>
				</a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="#">This Week</a></li>
						<li><a href="#">This Month</a></li>
						<li><a href="#">All</a></li>
					</ul>
				</li>
				<li><a href="#about">My Settings</a></li>
				<li><a href="#contact">Contact Admin</a></li>
				
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li class="active"><a href="#"><sec:authentication property="principal.username" /></a></li>
				<sec:authorize url="/admin">
					<li><a href="<c:url value="/admin"/>">Admin</a></li>
				</sec:authorize>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</nav>