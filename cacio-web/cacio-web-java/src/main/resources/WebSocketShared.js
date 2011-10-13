var webSocket;
var imgDecodeFunc;
var busy = false;
var imgDataStack = new Array();

function openWebSocket(ssid) {
    var socketURL = 'ws://'+window.location.host+'/WebSocket?subsessionid='+subSessionID;
    
    if(typeof WebSocket != "undefined") {
        webSocket = new WebSocket(socketURL);
    }else
    if(typeof MozWebSocket != "undefined") {
        webSocket = new MozWebSocket(socketURL);
    } else {
        alert("No suitable WebSocket implementation found, bailing out..");
    }
    
    webSocket.binaryType = "arraybuffer";
    webSocket.onmessage = onImageDataMsg;
}


function onImageDataMsg(m) {
    imgDataStack.push(m.data);    
    webSocketDataHandler();
}


function webSocketDataHandler() {
    if(imgDataStack.length > 0 && !busy) {
        busy = true;
        var currImgData = imgDataStack.shift();
        imgDecodeFunc(currImgData);
    }
}
