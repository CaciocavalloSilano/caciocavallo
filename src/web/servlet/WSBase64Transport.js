function initWSBase64() {
    imgDecodeFunc = decodeWSBase64;
    startRequestFunc = webSocketDataHandler;
    initEventFunc = initWebSocketEventStreamer;
    readCmdStreamFunc = readXHRBase64CommandStream;
    
    return "base64";
}

function dummyStartRequest() {}

function decodeWSBase64(imgData) {
    parts = imgData.split(":");
    loadBase64ImageData();         
}
