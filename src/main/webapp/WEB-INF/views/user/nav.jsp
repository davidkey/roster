<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- Fixed navbar -->
<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">Roster.Guru</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li id="navUser" class="active"><a href="<c:url value="/user/"/>">Home</a></li>
				<li id="navUpcomingDuties"><a href="<c:url value="/user/upcomingDuties"/>">Upcoming Duties <span id="upcomingDutiesCount" class="label label-primary"></span></a></li>
				<li id="navPreferences"><a href="<c:url value="/user/preferences"/>">Duty Preferences</a></li>
				<li><a href="#contact">Contact Admin</a></li>
			</ul>

			<ul class="nav navbar-nav navbar-right">

				<sec:authorize url="/admin">
					<li class="list-group-item-warning"><a href="<c:url value="/admin"/>">Admin</a></li>
				</sec:authorize>

				<li><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><sec:authentication property="principal.person.nameFirst" /> <span
						class="caret"></span></a>
					<ul class="dropdown-menu">
						<li role="presentation" class="disabled"><a href="#"><sec:authentication property="principal.username" /></a></li>
						<li role="separator" class="divider"></li>
						<li><a href="<c:url value='/user/changePassword' />">Change Password</a></li>
						<li role="separator" class="divider"></li>
						<li><a id="logoutLink" href="#">Log Out</a></li>
					</ul></li>

			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</nav>