let previousVerification = ["", ""];
let ok = [false, false];
let previous = [false, false];

const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");
const registerButtonId = "registerButton"


function checkVerificationEmail(e) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 0, input, length);
    check(1, length === 7 && regVerificationNumber.test(input), ok, previous, registerButtonId, success, true, true);
}

function checkVerificationSms(e) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 1, input, length);
    check(2, length === 7 && regVerificationNumber.test(input), ok, previous, registerButtonId, success, true, true);
}

function addWhiteSpace(e, index, input, length) {
    let previous = previousVerification[index];
    if (length === 3) {
        if (previous.charAt(previous.length - 1) === " ") {
            e.value = input.substring(0, length - 1);
        } else {
            e.value += " ";
        }
        input = e.value;
    }
    previousVerification[index] = input;
}

function atStart() {
    checkLanguage();
}


