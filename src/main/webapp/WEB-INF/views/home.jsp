<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en" class="full">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- build info: ${display_version} -->
	<jsp:include page="./shared/favicon.jsp" />
	
	<link href="<c:url value="/resources/css/themes/bootstrap.css"/>" rel="stylesheet">
	
	<link href="<c:url value="/resources/css/cover.css"/>" rel="stylesheet">
	
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->
	<script type="text/javascript">
	var WEB_ROOT = function(){
		return "${pageContext.request.contextPath}";
	}
	</script>
	<sec:csrfMetaTags />

<title>Roster Guru - Welcome!</title>
</head>
<body>
	<div class="site-wrapper">
		<div class="site-wrapper-inner">
			<div class="cover-container">
				<div class="masthead clearfix">
					<div class="inner">
						<h3 class="masthead-brand">Roster.Guru</h3>
						<nav>
							<ul class="nav masthead-nav">
								<li class="active"><a href="#">Home</a></li>
								<li><a href="#">Features</a></li>
								<li><a href="#">Contact</a></li>
								<li><a href="#">Sign Up</a></li>
								<li><a href="#">Log In</a></li>
							</ul>
						</nav>
					</div>
				</div>

				<div class="inner cover">
					<h1 class="cover-heading">Roster.Guru</h1>
					<p class="lead">Roster Guru is designed to take the pain out of generating and maintaining duty rosters.</p>
					<p class="lead">
						<a href="#" class="btn btn-lg btn-default">Learn more</a>
					</p>
				</div>

				<div class="mastfoot">
					<div class="inner">
						<p>
							By <a href="mailto:davidkey@gmail.com">David Key</a> &copy; 2015
						</p>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!--[if lt IE 9]>
    <script type='text/javascript' src='//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js'></script>
<![endif]-->
	<!--[if gte IE 9]><!-->
	<script src="<c:url value="/resources/jquery/jquery-2.1.4.min.js"/>"></script>
	<!--<![endif]-->
	<script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js"/>"></script>
</body>
</html>
