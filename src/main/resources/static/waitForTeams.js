$(function getTeams() {
    var sessionId = $('#page_content').data('sessionid');
    $.getJSON({
        url: `/rest/sessions/${sessionId}/teams`,
        method: 'GET',
        success: function(result) {
            console.log(result);
            // navigate to root
            result.forEach(addTeam);
        }
    });
});


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
        method: 'DELETE',
        success: function(result) {
            console.log(result);
            // Do something with the result
            teamListItem.parent().addClass('text-muted');
        }
    });
}

function endSession() {
    var sessionId = $('#page_content').data('sessionid');
    var key = $('#page_content').data('key');

    $.ajax({
        url: `/rest/sessions/${sessionId}?key=${key}`,
        method: 'DELETE',
        success: function(result) {
            console.log(result);
            // navigate to root
            window.location.href = new URL("/",document.baseURI).href;
        }
    });
}


$(function () {
    var sessionId = $('#page_content').data("sessionid");
    // connect to socket
    var socket = new SockJS('/quiz-websocket');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        displayStatus(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe(`/client/sessions/${sessionId}/teams`, function (envelope) {
            var msg = JSON.parse(envelope.body);
            if (msg.operation === 'entered') {
                addTeam(msg.team);
            } else if (msg.operation === 'left') {
                removeTeam(msg.team);
            }
        }, {}, function () {
            // on disconnect
            displayStatus(false);
        });
    });
});

function addTeam(team) {
    var sessionId = $("#page_content").data('sessionid');
    $("#teamList").append(
        `<li class="list-group-item" id="team-${team.id}">
                <span>&#${team.mascot};</span>
                <span>${team.name}</span>
                <button type="button" class="btn btn-danger float-right" data-team="${team.id}"
                        onclick="kickout($(this))" title="Kick them out!">
                    <i class="fas fa-times-circle"></i>
                </button>
                <button type="button" class="btn btn-light float-right" title="Send this URL to the team to play" 
                        onclick="copyLink('/ui/quiz/${sessionId}/teams/${team.id}/answer.html')">
                    <i class="far fa-clone"></i>
                </button>
                        
    </li>`);
}

function removeTeam(team) {
    $(`#team-${team.id}`).remove();
}

