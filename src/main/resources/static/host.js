
async function copyLink(clip) {
    var url = new URL(clip, document.baseURI).href
    await navigator.clipboard.writeText(url);
}

function kickout(teamListItem) {
    var sessionId = $('#page_content').data('sessionid');
    var key = $('#page_content').data('key');

    var team = teamListItem.data('team');
    // send JSON remove command
    $.ajax({
        url: `/rest/sessions/${sessionId}/teams/${team}?key=${key}`,
        type: 'DELETE',
        success: function(result) {
            console.log(result);
            // Do something with the result
            teamListItem.parent().remove();
        }
    });
}

function endSession() {
    var sessionId = $('#page_content').data('sessionid');
    var key = $('#page_content').data('key');

    $.ajax({
        url: `/rest/sessions/${sessionId}?key=${key}`,
        type: 'DELETE',
        success: function(result) {
            console.log(result);
            // navigate to root
            window.location.href = new URL("/",document.baseURI).href;
        }
    });
}

var stompClient = null;

$(function () {
    var statusIndicator = $('#connectionStatus')
    var sessionId = statusIndicator.data("sessionid");
    // connect to socket
    var socket = new SockJS('/quiz-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        statusIndicator.text("Connected");
        statusIndicator.attr('class', 'alert alert-success');
        console.log('Connected: ' + frame);
        stompClient.subscribe(`/client/sessions/${sessionId}`, function (envelope) {
            var msg = JSON.parse(envelope.body);
            if (msg.operation === 'entered') {
                addTeam(msg.team);
            } else if (msg.operation === 'left') {
                removeTeam(msg.team);
            }
        }, {}, function () {
            // on disconnect
            statusIndicator.text("Connection lost, refresh this page");
            statusIndicator.attr('class', 'alert alert-danger');
        });
    });
});

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function addTeam(team) {
    $("#teamList").append(
        `<li class="list-group-item" id="team-${team.id}">
                <span>&#${team.mascot};</span>
                <span>${team.name}</span>
                <button type="button" class="btn btn-danger float-right" data-team="${team.id}"
                        onclick="kickout($(this))" title="Kick them out!"><i class="fas fa-times-circle"></i></button>
    </li>`);
}

function removeTeam(team) {
    $(`#team-${team.id}`).remove();
}

