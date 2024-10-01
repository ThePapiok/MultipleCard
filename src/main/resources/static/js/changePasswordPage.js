let ok = [false, false, false, false];
let previous = [false, false, false, false];
const buttonId = "changePasswordButton";

function checkOldPassword(e) {
    const input = e.value;
    check(2, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
}
