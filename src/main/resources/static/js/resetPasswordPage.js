let previous1 = [false, false];
let previous2 = [true, true, false, false, false];
let ok1 = [false, false];
let ok2 = [true, true, false, false, false];
let success1 = {value: false}
let success2 = {value: false}
let previousVerification = "";
const buttonId1 = "codeButton";
const buttonId2 = "resetButton";
const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");

function cancelForm(){
    return false;
}

function checkCallingCode(e) {
    const input = e.value;
    check(1, (input !== ''), ok1, previous1, buttonId1, success1, false, false);
    checkPhone(document.getElementById("phoneInput"));
}

function checkPhone(e) {
    const input = e.value.toString().replaceAll(" ", "");
    phoneValue = input;
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(2, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok1, previous1, buttonId1, success1, true, false);
}

function checkVerificationSms(e) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, input, length);
    check(3, length === 7 && regVerificationNumber.test(input), ok2, previous2, buttonId2, success2, true, true);
}

function checkPassword(e) {
    const input = e.value;
    check(4, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok2, previous2, buttonId2, success2, true, true);
}

function checkRetypedPassword(e) {
    check(5, (e.value === document.getElementById("password").value), ok2, previous2, buttonId2, success2, true, true);
}

function setValueCallingCode(e) {
    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkCallingCode(valueSelect, buttonId1);
    callingCodeValue = valueSelect.value;
}

function getForm(e) {
    if (e.classList.contains("greenButton")) {
        const callingCode = document.getElementById("valueCallingCode").value;
        const phone = document.getElementById("phoneInput").value;
        fetch("/get_verification_number", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "callingCode": callingCode,
                "phone": phone
            })
        })
            .then(response => response.text())
            .then(response => {
                if(response !== "ok"){
                    document.getElementById("errorBefore").textContent = response;
                    document.getElementById("passwordForm").dataset.isSent = "false";
                    document.getElementById("valueCallingCode").disabled = false;
                    document.getElementById("phoneInput").disabled = false;
                }
                else{
                    document.getElementById("passwordForm").dataset.isSent = "true";
                    document.getElementById("error").textContent = "";
                    document.getElementById("errorBefore").textContent = "";
                    document.getElementById("valueCallingCode").disabled = true;
                    document.getElementById("phoneInput").disabled = true;
                }
            })
            .catch(error => {
                console.error(error);
            });
    }
}

function atStart() {
    checkCallingCode(document.getElementById("valueCallingCode"));
    document.getElementById("searchCallingCode").value = "";
    checkLanguage();
    checkVerificationSms(document.getElementById("verificationNumberSms"));
}

