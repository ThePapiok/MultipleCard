let ok = [false, false, false, false];
let previous = [false, false, false, false];
let previousVerification = ["", ""];
let disable = true;
const buttonId = "changePasswordButton";
const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");


function checkVerificationSms(e) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 1, input, length);
    check(1, length === 7 && regVerificationNumber.test(input), ok, previous, buttonId, success, true, true);
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

function atStart() {
    const urlParams =new URLSearchParams(window.location.search);
    if(urlParams.has("error")){
        enableInputs();
    }
    checkLanguage();
}


function enableInputs(){
    if(disable){
        disable = false;
        let inputs = document.getElementsByTagName("input");
        for(let i = 0; i < inputs.length; i++){
            inputs[i].disabled = false;
        }
    }
}


function getCode(phone) {
    enableInputs();
    fetch("/get_verification_number", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "callingCode": "",
            "phone": phone,
            "param": "codeSmsChange"
        })
    })
        .then(response => response.text())
        .then(response => {
            if(response !== "ok"){
                document.getElementById("error").textContent = response;
            }
        })
        .catch(error => {
            console.error(error);
        });
}