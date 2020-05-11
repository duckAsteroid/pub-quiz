$(function () {
    var page_content = $('#page_content')
    var sessionId = page_content.data("sessionid");
    // connect to socket to receive
    var socket = new SockJS('/quiz-websocket');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        displayStatus(true);
        console.log('Connected: ' + frame);
        // setup subscriptions
        stompClient.subscribe(`/client/sessions/${sessionId}`, function (envelope) {
            var msg = JSON.parse(envelope.body);
            // Respond to session event
            console.log("Session message: "+msg);
        }, {}, function () {
            // on disconnect
            displayStatus(false);
        });
    });
});
