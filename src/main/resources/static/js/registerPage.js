const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regFirstName = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$");
const regLastNameAndCity = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$");
const regHouseNumber = new RegExp("^[1-9][0-9]*([A-Z]|\/[1-9][0-9]*)?$");
const regApartmentNumber = new RegExp("^[1-9][0-9]*$");
const regPostalCode = new RegExp("^[0-9]{2}-[0-9]{3}$");
const regPhone = new RegExp("^\\+([0-9]{1,4} [0-9]{3} [0-9]{3} [0-9]{3}|[0-9]*)$")


let previousPostalCode = "";
let ok = [false, false, false, false, true, false, false, false, false, false, false];
let previous = [false, false, false, false, true, false, false, false, false, false, false];
let success = false;


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
    check(1, (input.length >= 2 && input.length <=15 && regFirstName.test(input)));
}

function checkLastName(e){
    const input = e.value;
    check(2, (input.length >= 2 && input.length <=40 && regLastNameAndCity.test(input)));
}

function checkStreet(e){
    const input = e.value;
    check(3, (input.length >= 2 && input.length <=40 && regLastNameAndCity.test(input)));
}

function checkHouseNumber(e){
    const input = e.value;
    check(4, (input.length >= 1 && input.length <=10 && regHouseNumber.test(input)));
}

function checkApartmentNumber(e){
    const input = e.value;
    check(5, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0));
}

function checkPostalCode(e){
    let input = e.value;
    let length = input.length;
    if(length === 2){
        if(previousPostalCode.charAt(previousPostalCode.length-1) === "-")
        {
            e.value = input.substring(0,length-1);
        }
        else{
            e.value+="-";
        }
        input = e.value;
    }
    previousPostalCode = input;
    check(6, (regPostalCode.test(input)));
}

function checkCity(e){
    const input = e.value;
    check(7, (input.length <= 40 && regLastNameAndCity.test(input)));
}

function checkCountry(e){
    const input = e.value;
    if(input.value === ''){
        ok[10] = false;
        if(previous[10] !== ok[10])
        {
            disableButton();
            previous[10] = false;
        }
    }
    else{
        ok[10] = true;
        if(previous[10] !== ok[10])
        {
            activeButton();
            previous[10] = false;
        }
    }
}

function checkPhone(e){
    const input = e.value;
    check(8, (input.length >= 11 && input.length <= 17 && regPhone.test(input)));
}

function  checkPassword(e){
    const input = e.value;
    check(9, (input.length >= 6 && input.length <= 25 && regPassword.test(input)));
}

function  checkRetypedPassword(e){
    check(10, (e.value === document.getElementById("passwordInput").value));
}


function activeButton(){
    if(ok[0] && ok[1] && ok[2] && ok[3] && ok[4] && ok[5] && ok[6] && ok[7] && ok[8] && ok[9] && ok[10]){
        let button = document.getElementById("nextButton");
        button.className = "greenButton";
        button.type = "submit";
        success = true;
    }
}

function disableButton(){
    if((!ok[0] || !ok[1] || !ok[2] || !ok[3] || !ok[4] || !ok[5] || !ok[6] || !ok[7] || !ok[8] || !ok[9] || !ok[10]) && success){
        let button = document.getElementById("nextButton");
        button.className = "grayButton";
        button.type = "button";
        success = false;
    }
}
function showValidation(e){
    let info = document.getElementById("validation" + e.parentElement.id);
    const cord = e.getBoundingClientRect();
    info.style.display = "inline";
    info.style.left = cord.right + window.scrollX + 'px';
    info.style.top = cord.top + window.scrollY  + 'px';
}

function hideValidation(e){
    document.getElementById("validation"+e.parentElement.id).style.display = "none";
}

function checkAll() {
    let inputs = document.getElementsByTagName("input");
    checkFirstName(inputs[0]);
    checkLastName(inputs[1]);
    checkStreet(inputs[2]);
    checkHouseNumber(inputs[3]);
    checkApartmentNumber(inputs[4]);
    checkPostalCode(inputs[5]);
    checkCity(inputs[6]);
    checkPhone(inputs[7]);
    checkCountry(document.getElementById("Country"));
}



