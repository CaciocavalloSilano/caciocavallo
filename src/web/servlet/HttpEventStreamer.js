var eventXmlHttpReq = new XMLHttpRequest();

function sendEvents() {
		if(eventBuffer.length > 0) {
		  var localEvents = 'subsessionid='+subSessionID+'&events=' + eventBuffer;
		  eventBuffer = '';
		  eventXmlHttpReq.open('POST', 'EventReceiver', true);
		  eventXmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		  eventXmlHttpReq.onreadystatechange = eventResponseHandler;
		  eventXmlHttpReq.send(localEvents);
		}else {
			window.setTimeout("sendEvents()", 10);
		}
}

function eventResponseHandler(){
   if (eventXmlHttpReq.readyState == 4) { 
	   sendEvents();
   }	
}


