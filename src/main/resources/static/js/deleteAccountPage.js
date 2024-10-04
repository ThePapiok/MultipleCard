let ok = [false];
let previous = [false];
const buttonId = "deleteButton";

window.addEventListener('beforeunload', function (event) {
    console.log("haha");
    if (buttons) {
        buttons = false;
        return;
    }
    console.log("haha2");
    event.preventDefault();
    event.returnValue = '';
    fetch("/reset_session", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "codeSmsParam": "codeSmsChange",
            "formObjectParam": null
        })
    }).catch((error) => {
        console.error(error);
    });
});