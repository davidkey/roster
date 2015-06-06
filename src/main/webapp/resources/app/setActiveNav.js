 $(function () { 
    	
    	function capitalize(input){
    		return input.replace(/(?:^|\s)\S/g, function(a) { return a.toUpperCase(); });
    	}

    	function setCurrentActivePage(){
    		var pathname = window.location.pathname; 
    		var pieces = pathname.split('/');
    		var section = pieces[2];
    		if(section === 'admin' && pieces[3]){
    			section  = pieces[3];
    		}
    		var activePage = 'navAdmin';
    		if(section){
    			activePage = 'nav' + capitalize(section);
    		}
    		
    		$('#navsidebar ul li.active').removeClass('active');
    		$('#navsidebar ul #' + activePage).addClass('active');
    	}
    	
    	setCurrentActivePage();
    });