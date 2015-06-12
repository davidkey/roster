function getCsrfHeaders(){
	var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
	var csrfHeader = $("meta[name='_csrf_header']").attr("content");
	var csrfToken = $("meta[name='_csrf']").attr("content");

	var headers = {};
	headers[csrfHeader] = csrfToken;
	return headers;
}