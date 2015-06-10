<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="../shared/header.jsp" />

<link rel="stylesheet" type='text/css' href="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.css">
<title>Messages</title>
</head>

<body>
<!-- modal -->
<div class="modal fade" id="msgModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body" id="msgModalBody">
        ...
      </div>
      <input type="hidden" id="msgModalMsgId" value="0"/>
      <input type="hidden" id="msgModalIsRead" value="false"/>
      <div class="modal-footer">
        <button id="closeMsg" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<!-- end modal -->
	<jsp:include page="../shared/nav.jsp" />

	<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		<h1 class="page-header">Messages</h1>
		<div class="table-responsive">
			<table class="table table-striped" id="eventTypeTable">
				<thead>
					<tr>
						<th style="display:none;">id</th>
						<th style="display:none;">read</th>
						<th>From</th>
						<th>Subject</th>
						<th>Received</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="message" items="${messages}">
						<c:choose>
							<c:when test="${message.read}">
						<tr class="active">
							</c:when>
							<c:otherwise>
						<tr class="warning">
							</c:otherwise>
						</c:choose>
						
							<td style="display:none;" class="msgId">${message.id}</td>
							<td style="display:none;" class="msgIsRead">${message.read}</td>
							<td>${message.sender}</td>
							<td>${message.subject}</td>
							<td class="msgTimestamp"><fmt:formatDate value="${message.timestampDate}" pattern="yyyy-MM-dd hh:mm aaa" /></td>
							<td><button id="readMsg" class="btn btn-sm btn-success">Read</button></td>
							<td><button id="deleteMsg" class="btn btn-sm btn-danger">Delete</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

	</div>

	<jsp:include page="../shared/footer.jsp" />
	<script type="text/javascript" src="<c:url value="/resources/app/messages.js"/>"></script>
</body>

</html>


