$(document).ready(function() {

	$('#autocomplete').devbridgeAutocomplete({
	    serviceUrl: WEB_ROOT() + '/api/search',
	    onSelect: function (suggestion) {
	    	window.location.href = WEB_ROOT() + suggestion.data;
	    }
	});
});