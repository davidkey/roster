 $(function () { 
    	
    	function capitalize(input){
    		return input.replace(/(?:^|\s)\S/g, function(a) { return a.toUpperCase(); });
    	}
    	
    	function setCurrentActivePage(){
    		var pieces = window.location.pathname.split('/');
    		var navBarId = pieces[2] === 'admin' ? 'navsidebar' : 'navbar';
    		var activePage = 'nav' + capitalize(pieces[3] ? pieces[3] : pieces[2]);

    		
    		$('#' + navBarId + ' ul li.active').removeClass('active');
    		$('#' + navBarId + ' ul #' + activePage).addClass('active');
    	}
    	
    	setCurrentActivePage();
    });