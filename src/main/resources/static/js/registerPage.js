const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regFirstName = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$");
const regLastNameAndCity = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$");
const regHouseNumber = new RegExp("^[1-9][0-9]*([A-Z]|\/[1-9][0-9]*)?$");
const regApartmentNumber = new RegExp("^[1-9][0-9]*$");
const regPostalCode = new RegExp("^[0-9]{2}-[0-9]{3}$");
const regPhone = new RegExp("^[1-9][0-9 ]*$");
const regEmail = new RegExp("^[A-za-z0-9-._]+@[A-z0-9a-z-.]+\\.[a-zA-z]{2,4}$");


let previousPostalCode = "";
let ok = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];
let success = false;
let country = false;
let callingCode = false;

// TODO - check number validation after bad

function check(i, con){
    if(con){
        ok[i-1] = true;
        if(previous[i-1] === false){
            document.getElementById("close"+i).style.display = "none";
            document.getElementById("check"+i).style.display = "inline";
        }
        if(ok[i-1] !== previous[i-1])
        {
            activeButton();
            previous[i-1] = true;
        }
    }
    else{
        ok[i-1] = false;
        if(previous[i-1] === true){
            document.getElementById("close"+i).style.display = "inline";
            document.getElementById("check"+i).style.display = "none";
        }
        if(ok[i-1] !== previous[i-1])
        {
            disableButton();
            previous[i-1] = false;
        }
    }
}
function checkFirstName(e){
    const input = e.value;
    check(1, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)));
}

function checkLastName(e){
    const input = e.value;
    check(2, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)));
}

function checkEmail(e){
    const input = e.value;
    check(3, (input.length >= 4 && input.length <= 30 && regEmail.test(input)));
}

function checkProvince(e){
    const input = e.value;
    check(4, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)));
}

function checkStreet(e){
    const input = e.value;
    check(5, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)));
}

function checkHouseNumber(e){
    const input = e.value;
    check(6, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)));
}

function checkApartmentNumber(e){
    const input = e.value;
    check(7, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0));
}

function checkPostalCode(e){
    let input = e.value;
    let length = input.length;
    if(length === 2){
        if(previousPostalCode.charAt(previousPostalCode.length-1) === "-")
        {
            e.value = input.substring(0, length - 1);
        }
        else{
            e.value+="-";
        }
        input = e.value;
    }
    previousPostalCode = input;
    check(8, (regPostalCode.test(input)));
}

function checkCity(e){
    const input = e.value;
    check(9, (input.length <= 40 && regLastNameAndCity.test(input)));
}

function checkSelect(input, index){
    if(input === ''){
        ok[index] = false;
        if(previous[index] !== ok[index])
        {
            disableButton();
            previous[index] = false;
        }
    }
    else{
        ok[index] = true;
        if(previous[index] !== ok[index])
        {
            activeButton();
            previous[index] = false;
        }
    }
}

function checkCountry(e){
    checkSelect(e.value, 12)
}

function checkCallingCode(e){
    checkSelect(e.value, 13)
    checkPhone(document.getElementById("phone"));
}

function checkPhone(e){
    const input = e.value.toString().replaceAll(" ", "");
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(10, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)));
}

function  checkPassword(e){
    const input = e.value;
    check(11, (input.length >= 6 && input.length <= 25 && regPassword.test(input)));
}

function  checkRetypedPassword(e){
    check(12, (e.value === document.getElementById("password").value));
}


function activeButton(){
    if(ok[0] && ok[1] && ok[2] && ok[3] && ok[4] && ok[5] && ok[6] && ok[7] && ok[8] && ok[9] && ok[10] && ok[11] && ok[12] && ok[13]){
        let button = document.getElementById("nextButton");
        button.className = "greenButton";
        button.type = "submit";
        success = true;
    }
}

function disableButton(){
    if((!ok[0] || !ok[1] || !ok[2] || !ok[3] || !ok[4] || !ok[5] || !ok[6] || !ok[7] || !ok[8] || !ok[9] || !ok[10] || !ok[11] || !ok[12] || !ok[13]) && success){
        let button = document.getElementById("nextButton");
        button.className = "grayButton";
        button.type = "button";
        success = false;
    }
}
function showValidation(e){
    let validation = e.parentElement.nextElementSibling;
    const cord = e.getBoundingClientRect();
    validation.style.display = "inline";
    validation.style.left = cord.right + window.scrollX + 'px';
    validation.style.top = cord.top + window.scrollY + 50 + 'px';
}

function hideValidation(e){
    e.parentElement.nextElementSibling.style.display = "none";
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
    checkPhone(document.getElementById("phone"));
    checkCountry(document.getElementById("valueCountry"));
    checkCallingCode(document.getElementById("valueCallingCode"))
    document.getElementById("searchCallingCode").value = "";
    document.getElementById("searchCountry").value = "";
}

function showOrHideSelect(cond, searchName, optionsName, upName, downName){
    let search = document.getElementById(searchName);
    let options = document.getElementById(optionsName);
    let up = document.getElementById(upName);
    let down = document.getElementById(downName);
    if(cond){
        search.style.display = "block";
        options.style.display = "block";
        up.style.display = "block";
        down.style.display = "none";
    }else{
        search.style.display = "none";
        options.style.display = "none";
        up.style.display = "none";
        down.style.display = "block";
    }
}

function showOrHideCountry(){
    country=!country;
    showOrHideSelect(country, "searchCountry", "optionsCountry", "up1", "down1")
}

function showOrHideCallingCode(){
    callingCode=!callingCode;
    showOrHideSelect(callingCode, "searchCallingCode", "optionsCallingCode", "up2", "down2")
}

function setValueCountry(e){
    let valueSelect = document.getElementById("valueCountry");
    valueSelect.value = e.textContent;
    checkCountry(valueSelect);
}

function setValueCallingCode(e){
    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkCallingCode(valueSelect);
}

function searchAllCountries(e){
    let text = e.value.toString().toLowerCase();
    let options = document.getElementsByClassName("optionCountry");
    let nothing = document.getElementById("nothingCountry");
    let option;
    let isFound = false;
    for(let i = 0; i < options.length; i++){
        option = options[i];
        if(option.textContent.toLowerCase().startsWith(text) || option.dataset.code.toLowerCase().startsWith(text)){
            option.style.display = "block";
            isFound = true;
        }
        else{
            option.style.display = "none";
        }
    }
    if(!isFound){
        nothing.style.display = "block";
    }
    else{
        nothing.style.display = "none";
    }

}

function searchAllCallingCodes(e){
    let text = e.value.toString();
    const char = text.charAt(0);
    let options = document.getElementsByClassName("optionCallingCode");
    let nothing = document.getElementById("nothingCallingCode");
    let option;
    let isFound = false;
    if(!isNaN(char)){
        text = "+" + text;
    }
    else if(char !== '+'){
        text = text.toLowerCase();
    }
    for(let i = 0; i < options.length; i++){
        option = options[i];
        if(option.textContent.startsWith(text) || option.dataset.code.toLowerCase().startsWith(text)){
            option.style.display = "block";
            isFound = true;
        }
        else{
            option.style.display = "none";
        }
    }
    if(!isFound){
        nothing.style.display = "block";
    }
    else{
        nothing.style.display = "none";
    }
}


