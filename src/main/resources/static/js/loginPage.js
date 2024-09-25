let previous = [false, false, false];
let ok = [false, false, false];
const buttonId = 'loginButton';

function checkCallingCode(e) {
    const input = e.value;
    callingCodeValue = input;
    check(1, (input !== ''), ok, previous, buttonId, success, false, true);
    checkPhone(document.getElementById("phone"), 2);
}

function atStart() {
    checkCallingCode(document.getElementById("valueCallingCode"));
    document.getElementById("searchCallingCode").value = "";
    document.getElementById("fullPhone").value = document.getElementById("valueCallingCode").value + document.getElementById("phone").value;
    checkLanguage();
}
