 $(function () { 
    	
    	function capitalize(input){
    		return input.replace(/(?:^|\s)\S/g, function(a) { return a.toUpperCase(); });
    	}
    	
    	function setCurrentActivePage(){
    		var pieces = window.location.pathname.split('/');
    		var navBarId = '';
    		var activePage = '';
    		
    		if(WEB_ROOT().length === 0){ /* for when app is deployed to ROOT context */
    			navBarId = pieces[1] === 'admin' ? 'navsidebar' : 'navbar';
    			activePage = 'nav' + capitalize(pieces[2] ? pieces[2] : pieces[1]);
    		} else {
    			navBarId = pieces[2] === 'admin' ? 'navsidebar' : 'navbar';
    			activePage = 'nav' + capitalize(pieces[3] ? pieces[3] : pieces[2]);
    		}
    		
    		$('#' + navBarId + ' ul li.active').removeClass('active');
    		$('#' + navBarId + ' ul #' + activePage).addClass('active');
    	}
    	
    	setCurrentActivePage();
    });