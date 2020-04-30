$(function loadQuiz() {
    var sessionId = $('#page_content').data('sessionid');
    $.getJSON({
        url: `/rest/sessions/${sessionId}/rounds`,
        method: 'GET',
        success: function(result) {
            console.log(result);
            // add each round to the NAV
        }
    });
})