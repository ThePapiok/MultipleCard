const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regPhone = new RegExp("^[1-9][0-9 ]*$");
const regEmail = new RegExp("^[A-za-z0-9-._]+@[A-z0-9a-z-.]+\\.[a-zA-z]{2,4}$");
const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");
const regFirstName = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$");
const regLastNameAndCity = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$");
const regHouseNumber = new RegExp("^[1-9][0-9]*([A-Z]|\/[1-9][0-9]*)?$");
const regApartmentNumber = new RegExp("^[1-9][0-9]*$");
const regPostalCode = new RegExp("^[0-9]{2}-[0-9]{3}$");
let previousPostalCode = "";
let country = false;
let callingCode = false;
let success = {value: false}
let previousVerification = ["", ""];
let callingCodeValue = "";
let phoneValue = "";

function check(i, con, ok, previous, id, success, hasCheck, isSubmit) {
    const previousCond = previous[i - 1];
    if (con) {
        ok[i - 1] = true;
        if (hasCheck && previousCond === false) {
            document.getElementById("close" + i).hidden = true;
            document.getElementById("check" + i).hidden = false;
        }
        if (previousCond === false) {
            activeButton(id, ok, success, isSubmit);
            previous[i - 1] = true;
        }
    } else {
        ok[i - 1] = false;
        if (hasCheck && previousCond === true) {
            document.getElementById("close" + i).hidden = false;
            document.getElementById("check" + i).hidden = true;
        }
        if (previousCond === true) {
            disableButton(id, ok, success, isSubmit);
            previous[i - 1] = false;
        }
    }
}

function showValidation(e, id) {
    let validation = document.getElementById(id).nextElementSibling;
    const cord = e.getBoundingClientRect();
    validation.style.display = "inline";
    validation.style.left = cord.right + window.scrollX + 'px';
    validation.style.top = cord.top + window.scrollY + 50 + 'px';
}

function hideValidation(id) {
    document.getElementById(id).nextElementSibling.style.display = "none";
}

function activeButton(id, ok, success, isSubmit) {
    let cond = true;
    let allNulls = true;
    for (let i = 0; i < ok.length; i++) {
        if (ok[i] !== null) {
            allNulls = false;
            if (!ok[i]) {
                cond = false;
                break;
            }
        }
    }
    if (allNulls) {
        cond = false;
    }
    if (cond) {
        let button = document.getElementById(id);
        button.className = "greenButton";
        success.value = true;
        if (isSubmit) {
            button.type = "submit";
        }
    }
}

function disableButton(id, ok, success, isSubmit) {
    let cond = false;
    let allNulls = true;
    for (let i = 0; i < ok.length; i++) {
        if (ok[i] !== null) {
            allNulls = false;
            if (!ok[i]) {
                cond = true;
                break;
            }
        }
    }
    if (allNulls) {
        cond = true;
    }
    if (cond && success.value) {
        let button = document.getElementById(id);
        button.className = "grayButton";
        success.value = false;
        if (isSubmit) {
            button.type = "button";
        }
    }
}

function showOrHideSelect(cond, searchName, optionsName, upName, downName) {
    let search = document.getElementById(searchName);
    let options = document.getElementById(optionsName);
    let up = document.getElementById(upName);
    let down = document.getElementById(downName);
    if (cond) {
        search.style.display = "block";
        search.focus();
        options.style.display = "block";
        up.hidden = false;
        down.hidden = true;
    } else {
        search.style.display = "none";
        options.style.display = "none";
        up.hidden = true;
        down.hidden = false;
    }
}

function searchAll(e, isCountry, value) {
    let optionSelect;
    let nothingSelect;
    let text;
    let char;
    if (isCountry) {
        optionSelect = "optionCountry" + value;
        nothingSelect = "nothingCountry" + value;
        text = e.value.toString().toLowerCase();
    } else {
        optionSelect = "optionCallingCode";
        nothingSelect = "nothingCallingCode";
        text = e.value.toString();
        char = text.charAt(0);
    }
    let options = document.getElementsByClassName(optionSelect);
    let nothing = document.getElementById(nothingSelect);
    let option;
    let isFound = false;
    if (!isCountry) {
        if (!isNaN(char)) {
            text = "+" + text;
        } else if (char !== '+') {
            text = text.toLowerCase();
        }
    }
    for (let i = 0; i < options.length; i++) {
        option = options[i];
        if ((isCountry && option.textContent.toLowerCase().startsWith(text) || option.dataset.code.toLowerCase().startsWith(text)) || (!isCountry && option.textContent.startsWith(text) || option.dataset.code.toLowerCase().startsWith(text))) {
            option.style.display = "block";
            isFound = true;
        } else {
            option.style.display = "none";
        }
    }
    nothing.hidden = isFound;
}

function showOrHideCountry(incValue, value) {
    country = !country;
    showOrHideSelect(country, "searchCountry" + incValue, "optionsCountry" + incValue, "up" + value, "down" + value)
}

function showOrHideCallingCode(upName, downName) {
    callingCode = !callingCode;
    showOrHideSelect(callingCode, "searchCallingCode", "optionsCallingCode", upName, downName)
}

function checkFirstName(e, value) {
    const input = e.value;
    check(value, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)), ok, previous, buttonId, success, true, true);
}

function checkLastName(e, value) {
    const input = e.value;
    check(value, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkEmail(e, value) {
    const input = e.value;
    check(value, (input.length >= 4 && input.length <= 30 && regEmail.test(input)), ok, previous, buttonId, success, true, true);
}

function checkProvince(e, value) {
    const input = e.value;
    check(value, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkStreet(e, value) {
    const input = e.value;
    check(value, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkHouseNumber(e, value) {
    const input = e.value;
    check(value, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)), ok, previous, buttonId, success, true, true);
}

function checkApartmentNumber(e, value) {
    const input = e.value;
    check(value, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0), ok, previous, buttonId, success, true, true);
}

function checkPostalCode(e, value) {
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
    check(value, (regPostalCode.test(input)), ok, previous, buttonId, success, true, true);
}


function checkCity(e, value) {
    const input = e.value;
    check(value, (input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPhone(e, value) {
    const input = e.value.toString().replaceAll(" ", "");
    phoneValue = input;
    setFullPhone()
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(value, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPassword(e, value) {
    const input = e.value;
    check(value, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
    checkRetypedPassword(document.getElementById("retypedPassword"), value + 1);
}

function checkRetypedPassword(e, value) {
    check(value, (e.value === document.getElementById("password").value), ok, previous, buttonId, success, true, true);
}

function checkSelect(e, index) {
    const input = e.value;
    check(index, (input !== ''), ok, previous, buttonId, success, false, true);
}

function setValueCountry(e, incValue, value) {
    let valueSelect = document.getElementById("valueCountry" + incValue);
    valueSelect.value = e.textContent;
    checkSelect(valueSelect, value);
}

function setValueCallingCode(e, value) {
    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkSelect(valueSelect, value);
    callingCodeValue = valueSelect.value;
    checkPhone(document.getElementById("phone"), value + 1);
}

function checkVerificationSms(e, value) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 0, input, length);
    check(value, length === 7 && regVerificationNumber.test(input), ok, previous, buttonId, success, true, true);
}

function setFullPhone() {
    let fullPhone = document.getElementById("fullPhone");
    if (fullPhone !== null) {
        fullPhone.value = callingCodeValue + phoneValue;
    }
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

function checkVerificationEmail(e, value) {
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 0, input, length);
    check(value, length === 7 && regVerificationNumber.test(input), ok, previous, buttonId, success, true, true);
}
