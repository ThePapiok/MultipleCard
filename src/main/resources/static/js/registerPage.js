const nextButtonId = "nextButton";

let previousPostalCode = "";
let ok = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];

function checkFirstName(e) {
    const input = e.value;
    check(1, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkLastName(e) {
    const input = e.value;
    check(2, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkEmail(e) {
    const input = e.value;
    check(3, (input.length >= 4 && input.length <= 30 && regEmail.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkProvince(e) {
    const input = e.value;
    check(4, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkStreet(e) {
    const input = e.value;
    check(5, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkHouseNumber(e) {
    const input = e.value;
    check(6, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkApartmentNumber(e) {
    const input = e.value;
    check(7, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0), ok, previous, nextButtonId, success, true, true);
}

function checkPostalCode(e) {
    let input = e.value;
    let length = input.length;
    if (length === 2) {
        if (previousPostalCode.charAt(previousPostalCode.length - 1) === "-") {
            e.value = input.substring(0, length - 1);
        } else {
            e.value += "-";
        }
        input = e.value;
    }
    previousPostalCode = input;
    check(8, (regPostalCode.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkCity(e) {
    const input = e.value;
    check(9, (input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkPhone(e) {
    const input = e.value.toString().replaceAll(" ", "");
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(10, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok, previous, nextButtonId, success, true, true);
}

function checkPassword(e) {
    const input = e.value;
    check(11, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, nextButtonId, success, true, true);
    checkRetypedPassword(document.getElementById("retypedPassword"));
}

function checkRetypedPassword(e) {
    check(12, (e.value === document.getElementById("password").value), ok, previous, nextButtonId, success, true, true);
}

function checkSelect(e, index) {
    const input = e.value;
    check(index, (input !== ''), ok, previous, nextButtonId, success, false, true);
}

function atStart() {
    checkFirstName(document.getElementById("firstName"));
    checkLastName(document.getElementById("lastName"));
    checkEmail(document.getElementById("email"));
    checkProvince(document.getElementById("province"));
    checkStreet(document.getElementById("street"));
    checkHouseNumber(document.getElementById("houseNumber"));
    checkApartmentNumber(document.getElementById("apartmentNumber"));
    checkPostalCode(document.getElementById("postalCode"));
    checkCity(document.getElementById("city"));
    checkSelect(document.getElementById("valueCountry"), 13)
    checkSelect(document.getElementById("valueCallingCode"), 14);
    checkPhone(document.getElementById("phone"));
    document.getElementById("searchCallingCode").value = "";
    document.getElementById("searchCountry").value = "";
    checkLanguage();
}

function setValueCountry(e) {
    let valueSelect = document.getElementById("valueCountry");
    valueSelect.value = e.textContent;
    checkSelect(valueSelect, 13);
}

function setValueCallingCode(e) {
    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkSelect(valueSelect, 14);
    checkPhone(document.getElementById("phone"));
}





