const loginButtonId = "loginButton";

let previous = [false, false, false];
let ok = [false, false, false];
let success = {value: false}
let callingCode = false;
let callingCodeValue = "";
let phoneValue = "";

function checkPhone(e) {
    const input = e.value.toString().replaceAll(" ", "");
    phoneValue = input;
    setFullPhone();
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(1, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok, previous, loginButtonId, success, false);
}

function checkPassword(e) {
    const input = e.value;
    check(2, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, loginButtonId, success, false);

}

function checkCallingCode(e) {
    const input = e.value;
    check(3, (input !== ''), ok, previous, loginButtonId, success, true);
    checkPhone(document.getElementById("phoneInput"));
}

function atStart() {
    checkCallingCode(document.getElementById("valueCallingCode"));
    document.getElementById("searchCallingCode").value = "";
}

function showOrHideCallingCode() {
    callingCode = !callingCode;
    showOrHideSelect(callingCode, "searchCallingCode", "optionsCallingCode", "up1", "down1")
}

function setValueCallingCode(e) {

    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkCallingCode(valueSelect);
    callingCodeValue = valueSelect.value;
    setFullPhone();
}

function setFullPhone() {
    document.getElementById("fullPhone").value = callingCodeValue + phoneValue;
}

