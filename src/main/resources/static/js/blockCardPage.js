let ok = [false];
let previous = [false];
const buttonId = "blockButton";

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
            "codeSmsParam": "codeSmsBlock",
            "formObjectParam": "order"
        })
    }).catch((error) => {
        console.error(error);
    });
});
