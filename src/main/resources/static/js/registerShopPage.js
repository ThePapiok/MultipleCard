const buttonId = "nextButton";
let ok = [false, false, false, false, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, false, false, false, false, false];

function atStart() {
    checkFirstName(document.getElementById("firstName"), 1);
    checkLastName(document.getElementById("lastName"), 2);
    checkEmail(document.getElementById("email"), 3);
    checkCallingCode(document.getElementById("valueCallingCode"), 4);
    checkShopName(document.getElementById("name"));
    checkAccountNumber(document.getElementById("accountNumber"));
    checkPoints(0);
    checkLanguage();
}

function checkShopName(e) {
    const input = e.value;
    check(8, (input.length >= 2 && input.length <= 30), ok, previous, buttonId, success, true, true);
}

function checkAccountNumber(e) {
    const input = e.value;
    check(9, (input.length === 26 && regAccountNumber.test(input)), ok, previous, buttonId, success, true, true);
}

