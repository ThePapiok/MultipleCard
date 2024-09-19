let previous = [false, false, false];
let ok = [false, false, false];
let callingCodeValue = "";
let phoneValue = "";
const buttonId = 'loginButton';

function checkCallingCode(e) {
    const input = e.value;
    check(1, (input !== ''), ok, previous, buttonId, success, false, true);
    checkPhone(document.getElementById("phoneInput"));
}

function checkPhone(e) {
    const input = e.value.toString().replaceAll(" ", "");
    phoneValue = input;
    setFullPhone();
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(2, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPassword(e) {
    const input = e.value;
    check(3, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
}

function setValueCallingCode(e) {

    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkCallingCode(valueSelect, buttonId);
    callingCodeValue = valueSelect.value;
    setFullPhone();
}

function setFullPhone() {
    document.getElementById("fullPhone").value = callingCodeValue + phoneValue;
}

function atStart() {
    checkCallingCode(document.getElementById("valueCallingCode"));
    document.getElementById("searchCallingCode").value = "";
    document.getElementById("fullPhone").value = document.getElementById("valueCallingCode").value + document.getElementById("phoneInput").value;
    checkLanguage();
}

