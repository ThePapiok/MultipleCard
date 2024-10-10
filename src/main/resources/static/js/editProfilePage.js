let ok = [false];
let previous = [false];
const buttonId = "saveButton";

window.addEventListener('beforeunload', function (event) {
    if (buttons) {
        buttons = false;
        return;
    }
    event.preventDefault();
    event.returnValue = '';
    fetch("/reset_session", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "codeSmsParam": "codeSmsEdit",
            "formObjectParam": "edit"
        })
    }).catch((error) => {
        console.error(error);
    });
});