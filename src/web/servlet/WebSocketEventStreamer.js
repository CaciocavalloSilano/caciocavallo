function initWebSocketEventStreamer() {
    openWebSocket();
    sendEventsWebSocket();
}

function sendEventsWebSocket() {
    
    if(eventBuffer.length > 0 && webSocket.readyState == 1) {  
        webSocket.send(eventBuffer);
        eventBuffer = '';
    }
    
    window.setTimeout("sendEventsWebSocket()", 10);
}
