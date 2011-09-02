var maxEventRequests = 4;


var dispatchDelay = 10;
var curEventId = 1;
var idleRequestArray = new Array();

//TODO: Move into some pretty init routine
for(var i=0; i < maxEventRequests; i++) {
    idleRequestArray.push(new XMLHttpRequest());
}


function chromeLog(msg) {
  if(console && console.log) {
    console.log(msg);    
  }    
}

function sendEvents() {
        if(idleRequestArray.length == 0) {
            dispatchDelay += 10;
            chromeLog("Dispatch increased: "+dispatchDelay);
        } else 
        if(idleRequestArray.length == 5) {
            dispatchDelay = Math.max(dispatchDelay-10, 10);
            if(dispatchDelay != 10) {
                chromeLog("Dispatch lowered: "+dispatchDelay);
            }
        }
    
		if(eventBuffer.length > 0) {      
            if(idleRequestArray.length > 0) {
              var localEvents = 'subsessionid='+subSessionID+'&eid='+curEventId+'&events=' + eventBuffer;
              var eventXmlHttpReq = idleRequestArray.pop();
              eventBuffer = '';
              curEventId++;
              
              eventXmlHttpReq.open('POST', 'EventReceiver', true);
              eventXmlHttpReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
              eventXmlHttpReq.onreadystatechange = eventResponseHandler;
              eventXmlHttpReq.send(localEvents);
          }
        }
			 
        window.setTimeout("sendEvents()", dispatchDelay);
}

function eventResponseHandler(){
   if (this.readyState == 2) {
       this.abort();
       idleRequestArray.push(this);
   }	
}


