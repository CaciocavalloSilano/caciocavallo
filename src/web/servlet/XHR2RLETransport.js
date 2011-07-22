function initXHR2Rle() {
	initXHR2Shared();
	responseHandlerFunc = handleXHR2RLEResponse;
}

function handleXHR2RLEResponse() {
	var buffer = xmlhttpreq.response ? xmlhttpreq.response : xmlhttpreq.mozResponseArrayBuffer;
	intArray = new Uint8Array(buffer);

	decodeRLEImageData();
	interpretCommandBuffer();
}
