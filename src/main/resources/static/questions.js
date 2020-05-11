/**
 * This file is the brains behind the question nav bar
 */

$(function loadQuiz() {
    var sessionId = $('#page_content').data('sessionid');
    $.getJSON({
        url: `/rest/sessions/${sessionId}/rounds`,
        method: 'GET',
        success: function(result) {
            console.log("rounds:" + result);
            buildTree(result);
            $.getJSON({
                url: `/rest/sessions/${sessionId}/questions/current`,
                method: 'GET',
                success: function(result) {
                    console.log("current: "+result);
                    setCurrentQuestion(result);
                }
            });
        }
    });

});

function buildTree(rounds) {
    var roundListElement = $('#rounds');
    roundListElement.empty();
    rounds.forEach(function (round, index) {
        var questions = "";
        for(let i =0; i < round.questionCount; i++) {
            questions += `<li id="r${index}q${i}" class="nav-question list-group-item disabled" data-questionid="${index}.${i}">Question ${i + 1}</li>`;
        }
        roundListElement.append(
            `<li id="r${index}" class="nav-round list-group-item disabled">
                <i class="icon fas fa-plus"></i>
                <span class="nav-round-no font-weight-light">Round ${index} - </span>
                <span class="nav-round-title">${round.title}</span>
                <ul class="list-group list-group-flush questions nested">
                    ${questions}
                </ul>
        </li>`);
    });

    $('.nav-round').on("click", function() {
        var icon = $(this).children(".icon");
        icon.toggleClass("fa-plus");
        icon.toggleClass("fa-minus");

        $(this).children(".nested").toggleClass("active");
    });

    $('.nav-question').on("click", function () {
        if ($(this).hasClass('disabled')) {
            // do nothing
            return false;
        }
        else {
            alert($(this).data('questionid'));
            return false;
        }
    })
}

function setCurrentQuestion(questionId) {
    var round = questionId['round'];
    // enable all previous rounds... (if any)
    for (let r = 0; r < round; r++) {
        var roundElement = $(`#r${r}`);
        roundElement.removeClass('disabled');
        roundElement.select('.question').removeClass('disabled list-group-item-success');
    }
    // enable the current round
    var roundElement = $(`#r${round}`);
    roundElement.removeClass('disabled');
    // now the previous questions in the current round (if any)
    var question = questionId['question'];
    for(let q = 0; q < question; q++) {
        var questionElement = $(`#r${round}q${q}`);
        questionElement.removeClass('disabled list-group-item-success');
    }
    // now the selected question
    var selectedQuestionElement = $(`#r${round}q${question}`);
    selectedQuestionElement.removeClass('disabled');
    selectedQuestionElement.addClass('list-group-item-success');
}

function setStatus(status) {
    var statusIndicator = $('#quizSessionStatus');
    if (status === 'playing') {
        statusIndicator.text("Playing...");
        statusIndicator.addClass('text-success');
    }
    else {
        statusIndicator.text(status);
        statusIndicator.removeClass('text-success');
    }
}