let ok = [false, false];
let previous = [false, false];
const buttonId = "saveButton";

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
            "codeSmsParam": "codeSmsEditShop",
            "formObjectParam": "editShop"
        })
    }).catch((error) => {
        console.error(error);
    });
});