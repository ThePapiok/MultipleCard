const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regPhone = new RegExp("^[1-9][0-9 ]*$");
const regEmail = new RegExp("^[A-za-z0-9-._]+@[A-z0-9a-z-.]+\\.[a-zA-z]{2,4}$");
let country = false;
let callingCode = false;
let success = {value: false}

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
    if (!isFound) {
        nothing.hidden = false;
    } else {
        nothing.hidden = true;
    }
}

function showOrHideCountry(incValue, value) {
    country = !country;
    showOrHideSelect(country, "searchCountry" + incValue, "optionsCountry" + incValue, "up" + value, "down" + value)
}

function showOrHideCallingCode(upName, downName) {
    callingCode = !callingCode;
    showOrHideSelect(callingCode, "searchCallingCode", "optionsCallingCode", upName, downName)
}

