$(function () {
    var page_content = $('#page_content')
    var sessionId = page_content.data("sessionid");
    var teamId = page_content.data("teamid");
    // connect to socket to receive
    var socket = new SockJS('/quiz-websocket');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        displayStatus(true);
        console.log('Connected: ' + frame);
        // setup subscriptions
        stompClient.subscribe(`/client/sessions/${sessionId}/teams`, function (envelope) {
            var msg = JSON.parse(envelope.body);
            console.log("Team message: "+msg);
            // Respond to team events
            if(msg.team.id === teamId) {
                if (msg.operation === "left") {
                    window.alert("You have been kicked out!");
                    window.location.href = new URL("../../play.html",document.baseURI).href;
                }
            }
        }, {}, function () {
            // on disconnect
            displayStatus(false);
        });

        stompClient.subscribe(`/client/sessions/${sessionId}`, function (envelope) {
            var msg = JSON.parse(envelope.body);
            // Respond to session event
            console.log("Session message: "+msg);
        }, {}, function () {
            // on disconnect
            displayStatus(false);
        });
    });

    // get quiz session and quiz
    $.getJSON()
});

function showWaitingForStart() {

}

function showQuestions() {

}