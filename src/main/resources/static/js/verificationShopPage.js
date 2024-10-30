let ok = [false, false, false];
let previous = [false, false, false];
const buttonId = "registerButton";

function atStart() {
    checkLanguage();
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
            "codeSmsParam": "codeSmsRegisterShop",
            "formObjectParam": "register"
        })
    }).catch((error) => {
        console.error(error);
    });
});
