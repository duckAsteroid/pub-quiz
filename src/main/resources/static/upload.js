async function submitForm() {

    // get the file selected by the end user
    var my_file = getFileInput();

    var reader = new FileReader();
    reader.readAsText(my_file, 'UTF-8');
    reader.onload = shipOff;

}

function shipOff(event) {
    var result = event.target.result;
    var host = document.getElementById("host-field").textContent;
    $.ajax('/start', {
        type: "POST",
        timeout: 50000,
        headers: {"host": host},
        data: result,
        success: function (data) {
            alert('success');
            return false;
        }
    });


}

/**
 * Apply the danger style to the alert control and show the specified message.
 *
 * @param {*} msg - message string to display
 */
function reportError(msg) {
    console.log(msg);
    showAlert();
    $('#my-alert').removeClass("alert-success");
    $('#my-alert').addClass("alert-danger");
    $('#alert-bold').html("Warning!");
    $('#alert-msg').html(msg);
}

/**
 * Hide the alert control.
 */
function hideAlert() {
    $('#my-alert').removeClass("show");
    $('#my-alert').addClass("hide");
}

/**
 * Show the alert control.
 */
function showAlert() {
    $('#my-alert').removeClass("hide");
    $('#my-alert').addClass("show");
}

/**
 * Retrieves the file selected by the end user.
 * Returns a file object if a file has been selected. Otherwise returns null.
 */
function getFileInput() {
    var file = document.getElementById("file-field").files[0];
    if (file === undefined) {
        throw new Error("Please select a file to upload.");
    }

    return file;
}