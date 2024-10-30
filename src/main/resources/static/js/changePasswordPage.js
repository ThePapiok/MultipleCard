let ok = [false, false, false, false];
let previous = [false, false, false, false];
const buttonId = "changePasswordButton";

function checkOldPassword(e) {
    const input = e.value;
    check(2, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
}

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
            "codeSmsParam": "codeSmsDelete",
            "formObjectParam": null
        })
    }).catch((error) => {
        console.error(error);
    });
});
