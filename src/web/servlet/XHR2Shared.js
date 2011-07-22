function initXHR2Shared() {
	startRequestFunc = StartXHR2Request;
	readCmdStreamFunc = readBinCommandStream;
}

function StartXHR2Request(subSessionID) {
  xmlhttpreq = new XMLHttpRequest();
  xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
  xmlhttpreq.responseType = 'arraybuffer';

  xmlhttpreq.onreadystatechange = xhrSuccessHandler;
  xmlhttpreq.send(null);
}
