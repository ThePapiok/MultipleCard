let ok = [null, null, null, null, null, null, null, null, null];
let previous = [null, null, null, null, null, null, null, null, null];
let firstName;
let lastName;
let province;
let street;
let houseNumber;
let apartmentNumber;
let postalCode;
let city;
let countryInput;
const buttonId = "saveButton";

function checkOnlyIfOther(input, cond, i, startInput, hasCheck, e) {
    if (input === startInput) {
        deleteCheck(i, hasCheck, e);
    } else {
        e.style.color = "black";
        previous[i - 1] = !cond;
        check(i, cond, ok, previous, buttonId, success, hasCheck, true);
    }
}

function checkFirstName(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)), 1, firstName, true, e);
}

function checkLastName(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), 2, lastName, true, e);
}

function checkStreet(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), 3, street, true, e);
}

function checkHouseNumber(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)), 4, houseNumber, true, e);
}

function checkApartmentNumber(e) {
    const input = e.value;
    checkOnlyIfOther(input, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0), 5, apartmentNumber, true, e);
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
    checkOnlyIfOther(input, ((regPostalCode.test(input)) || input.length === 0), 6, postalCode, true, e);
}

function checkCity(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length <= 40 && regLastNameAndCity.test(input)), 7, city, true, e);
}

function checkProvince(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), 8, province, true, e);
}

function checkCountry(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input !== ''), 9, countryInput, false, e);
}

function setValueCountry(e) {
    let valueSelect = document.getElementById("valueCountry");
    valueSelect.value = e.textContent;
    checkCountry(valueSelect);
}

function deleteCheck(i, hasCheck, e) {
    if (hasCheck) {
        document.getElementById("close" + i).hidden = true;
        document.getElementById("check" + i).hidden = true;
    }
    e.style.color = "lightgray";
    ok[i - 1] = null;
    previous[i - 1] = null;
    let cond = true;
    activeButton(buttonId, ok, success, true);
    disableButton(buttonId, ok, success, true);
    for (let j = 0; j <= ok.length; j++) {
        if (ok[j] !== null && ok[j] === false) {
            cond = false;
        }
    }
}

function atStart() {
    firstName = document.getElementById("firstName").value;
    lastName = document.getElementById("lastName").value;
    province = document.getElementById("provinceInput").value;
    street = document.getElementById("streetInput").value;
    houseNumber = document.getElementById("houseNumberInput").value;
    apartmentNumber = document.getElementById("apartmentNumberInput").value;
    postalCode = document.getElementById("postalCodeInput").value;
    city = document.getElementById("cityInput").value;
    countryInput = document.getElementById("valueCountry").value;
    if (document.getElementById("cardNotFound") !== null) {
        document.getElementById("card").style.opacity = "50%";
    }
    checkLanguage();
}
