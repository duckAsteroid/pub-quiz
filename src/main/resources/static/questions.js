$(function loadQuiz() {
    var sessionId = $('#page_content').data('sessionid');
    $.getJSON({
        url: `/rest/sessions/${sessionId}/rounds`,
        method: 'GET',
        success: function(result) {
            console.log(result);
            buildTree(result);
        }
    });
});

function buildTree(rounds) {
    var roundListElement = $('#rounds');
    roundListElement.empty();
    rounds.forEach(function (round, index) {
        var questions = "";
        for(let i =0; i < round.questionCount; i++) {
            questions += `<li class="nav-question" data-questionid="${index}.${i}">Question ${i + 1}</li>`;
        }
        roundListElement.append(
            `<li class="nav-round" id="round-${index}">
                <span>Round ${index} - </span>
                <span>${round.title}</span>
                <ul class="questions nested">
                    ${questions}
                </ul>
        </li>`);
    });

    $('.nav-round').on("click", function() {
       $(this).children(".nested").toggleClass("active");
    });

    $('.nav-question').on("click", function () {
        alert($(this).data('questionid'));
        return false;
    })
}

function toggleRound(round) {

}