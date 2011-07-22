var xmlhttpreq;
var responseHandlerFunc;

function xhrSuccessHandler() {
	if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  responseHandlerFunc();
	  } else {
		  //TODO: Alert
		  //startRequestFunc(subSessionID);
	  }
  }
}
