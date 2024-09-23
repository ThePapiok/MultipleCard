let ok = [false];
let previous = [false];

function checkVerificationSms(e, buttonId) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, input, length);
    check(1, length === 7 && regVerificationNumber.test(input), ok, previous, buttonId, success, true, true);
}