<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
      </div>
    </div>

    <script src="<c:url value="/resources/jquery/jquery-2.1.4.min.js"/>"></script>
    <script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js"/>"></script>
    <script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.7/js/jquery.dataTables.js"></script>
    <script>
    $(function () { 
    	
    	function capitalize(input){
    		return input.replace(/(?:^|\s)\S/g, function(a) { return a.toUpperCase(); });
    	}

    	function setCurrentActivePage(){
    		var pathname = window.location.pathname; 
    		var pieces = pathname.split('/');
    		var section = pieces[2];
    		if(section === 'admin'){
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
    </script>