let ok = [false, false, false, false];
let previous = [false, false, false, false];
const buttonId = "orderButton";
const regPin = new RegExp("^[0-9]*$");
const regCardName = new RegExp("^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]*$");


function checkCardName(e){
    const input = e.value;
    check(1, (input.length >= 4 && input.length <= 9 && regCardName.test(input)), ok, previous, buttonId, success, true, true);
}


function  checkPin(e){
    const input = e.value;
    check(2, (input.length = 4 && regPin.test(input)), ok, previous, buttonId, success, true, true);
    checkRetypedPin(document.getElementById("retypedPin"));

}

function checkRetypedPin(e){
    check(3, (e.value === document.getElementById("pin").value), ok, previous, buttonId, success, true, true);
}

function checkVerificationSms(e) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, input, length);
    check(4, length === 7 && regVerificationNumber.test(input), ok, previous, buttonId, success, true, true);
}