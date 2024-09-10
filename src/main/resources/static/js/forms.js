const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regPhone = new RegExp("^[1-9][0-9 ]*$")
let country = false;
let callingCode = false;

function check(i, con, ok, previous, id, success, hasCheck, isSubmit) {
    const previousCond = previous[i - 1];
    if (con) {
        ok[i - 1] = true;
        if (hasCheck && previousCond === false) {
            document.getElementById("close" + i).style.display = "none";
            document.getElementById("check" + i).style.display = "inline";
        }
        if (previousCond === false) {
            activeButton(id, ok, success, isSubmit);
            previous[i - 1] = true;
        }
    } else {
        ok[i - 1] = false;
        if (hasCheck && previousCond === true) {
            document.getElementById("close" + i).style.display = "inline";
            document.getElementById("check" + i).style.display = "none";
        }
        if (previousCond === true) {
            disableButton(id, ok, success, isSubmit);
            previous[i - 1] = false;
        }
    }
}

function showValidation(e) {
    let validation = e.parentElement.nextElementSibling;
    const cord = e.getBoundingClientRect();
    validation.style.display = "inline";
    validation.style.left = cord.right + window.scrollX + 'px';
    validation.style.top = cord.top + window.scrollY + 50 + 'px';
}

function hideValidation(e) {
    e.parentElement.nextElementSibling.style.display = "none";
}

function activeButton(id, ok, success, isSubmit) {
    let cond = true;
    for (let i = 0; i < ok.length; i++) {
        if (ok[i] === false) {
            cond = false;
            break;
        }
    }
    if (cond) {
        let button = document.getElementById(id);
        button.className = "greenButton";
        success.value = true;
        if(isSubmit){
            button.type = "submit";
        }
    }
}

function disableButton(id, ok, success, isSubmit) {
    let cond = false;
    for (let i = 0; i < ok.length; i++) {
        if (ok[i] === false) {
            cond = true;
            break;
        }
    }
    if (cond && success.value) {
        let button = document.getElementById(id);
        button.className = "grayButton";
        success.value = false;
        if(isSubmit){
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
        options.style.display = "block";
        up.style.display = "block";
        down.style.display = "none";
    } else {
        search.style.display = "none";
        options.style.display = "none";
        up.style.display = "none";
        down.style.display = "block";
    }
}

function searchAll(e, isCountry) {
    let optionSelect;
    let nothingSelect;
    let text;
    let char;
    if (isCountry) {
        optionSelect = "optionCountry";
        nothingSelect = "nothingCountry";
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
        nothing.style.display = "block";
    } else {
        nothing.style.display = "none";
    }
}

function showOrHideCountry(upName, downName) {
    country = !country;
    showOrHideSelect(country, "searchCountry", "optionsCountry", upName, downName)
}

function showOrHideCallingCode(upName, downName) {
    callingCode = !callingCode;
    showOrHideSelect(callingCode, "searchCallingCode", "optionsCallingCode", upName, downName)
}

