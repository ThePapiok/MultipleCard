const regFirstName = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$");
const regLastNameAndCity = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$");
const regHouseNumber = new RegExp("^[1-9][0-9]*([A-Z]|\/[1-9][0-9]*)?$");
const regApartmentNumber = new RegExp("^[1-9][0-9]*$");
const regPostalCode = new RegExp("^[0-9]{2}-[0-9]{3}$");
const regEmail = new RegExp("^[A-za-z0-9-._]+@[A-z0-9a-z-.]+\\.[a-zA-z]{2,4}$");
const nextButtonId = "nextButton";

let previousPostalCode = "";
let ok = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];
let success = {value: false};
let country = false;
let callingCode = false;

function checkFirstName(e) {
    const input = e.value;
    check(1, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)), ok, previous, nextButtonId, success, true);
}

function checkLastName(e) {
    const input = e.value;
    check(2, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true);
}

function checkEmail(e) {
    const input = e.value;
    check(3, (input.length >= 4 && input.length <= 30 && regEmail.test(input)), ok, previous, nextButtonId, success, true);
}

function checkProvince(e) {
    const input = e.value;
    check(4, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true);
}

function checkStreet(e) {
    const input = e.value;
    check(5, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true);
}

function checkHouseNumber(e) {
    const input = e.value;
    check(6, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)), ok, previous, nextButtonId, success, true);
}

function checkApartmentNumber(e) {
    const input = e.value;
    check(7, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0), ok, previous, nextButtonId, success, true);
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
    check(8, (regPostalCode.test(input)), ok, previous, nextButtonId, success, true);
}

function checkCity(e) {
    const input = e.value;
    check(9, (input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, nextButtonId, success, true);
}

function checkPhone(e) {
    const input = e.value.toString().replaceAll(" ", "");
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(10, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok, previous, nextButtonId, success, true);
}

function checkPassword(e) {
    const input = e.value;
    check(11, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, nextButtonId, success, true);
}

function checkRetypedPassword(e) {
    check(12, (e.value === document.getElementById("password").value), ok, previous, nextButtonId, success, true);
}

function checkSelect(e, index) {
    const input = e.value;
    check(index, (input !== ''), ok, previous, nextButtonId, success, false);
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

function showOrHideCountry() {
    country = !country;
    showOrHideSelect(country, "searchCountry", "optionsCountry", "up1", "down1")
}

function showOrHideCallingCode() {
    callingCode = !callingCode;
    showOrHideSelect(callingCode, "searchCallingCode", "optionsCallingCode", "up2", "down2")
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





