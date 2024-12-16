let ok = [false, false, false, false];
let previous = [false, false, false, false];
const buttonId = "orderButton";
const regPin = new RegExp("^[0-9]*$");

function checkCardName(e) {
    const input = e.value;
    check(1, (input.length >= 4 && input.length <= 9 && regCardName.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPin(e) {
    const input = e.value;
    check(2, (input.length = 4 && regPin.test(input)), ok, previous, buttonId, success, true, true);
    checkRetypedPin(document.getElementById("retypedPin"));
}

function checkRetypedPin(e) {
    check(3, (e.value === document.getElementById("pin").value), ok, previous, buttonId, success, true, true);
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
            "codeSmsParam": "codeSmsOrder",
            "formObjectParam": "order"
        })
    }).catch((error) => {
        console.error(error);
    });
});
