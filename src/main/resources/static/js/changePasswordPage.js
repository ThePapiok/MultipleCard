let ok = [false, false, false, false];
let previous = [false, false, false, false];
const buttonId = "changePasswordButton";

function checkVerificationSms(e) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, input, length);
    check(1, length === 7 && regVerificationNumber.test(input), ok, previous, buttonId, success, true, true);
}

function checkOldPassword(e) {
    const input = e.value;
    check(2, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPassword(e) {
    const input = e.value;
    check(3, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
    checkRetypedPassword(document.getElementById("retypedPassword"));
}

function checkRetypedPassword(e) {
    check(4, (e.value === document.getElementById("newPassword").value), ok, previous, buttonId, success, true, true);
}
