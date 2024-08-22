let previous = [false, false, false];
let ok = [false, false, false];
let success = false;
let areaCode = false;
let areaCodeValue = "";
let phoneValue = "";

const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regPhone = new RegExp("^[1-9][0-9 ]*$")

// TODO make unique this and css

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

function checkPhone(e){
    const input = e.value.toString().replaceAll(" ", "");
    phoneValue = input;
    setFullPhone();
    const areaCodeLength = document.getElementById("valueAreaCode").value.length;
    check(1, (input.length >= 8 && input.length + areaCodeLength <= 16 && regPhone.test(input)));
}

function  checkPassword(e){
    const input = e.value;
    check(2, (input.length >= 6 && input.length <= 25 && regPassword.test(input)));
}

function checkAreaCode(e){
    const input = e.value;
    if(input === ''){
        ok[2] = false;
        if(previous[2] !== ok[2])
        {
            disableButton();
            previous[2] = false;
        }
    }
    else{
        ok[2] = true;
        if(previous[2] !== ok[2])
        {
            activeButton();
            previous[2] = false;
        }
    }
    checkPhone(document.getElementById("phoneInput"));
}

function activeButton(){
    if(ok[0] && ok[1] && ok[2]){
        let button = document.getElementById("loginButton");
        button.className = "greenButton";
        button.type = "submit";
        success = true;
    }
}

function disableButton(){
    if((!ok[0] || !ok[1] || !ok[2]) && success){
        let button = document.getElementById("loginButton");
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

function hideValidation(e) {
    document.getElementById("validation"+e.parentElement.id).style.display = "none";
}

function checkAll(){
    checkPhone(document.getElementById("phoneInput"));
}

function showOrHideAreaCode(){
    areaCode=!areaCode;
    let search = document.getElementById("searchAreaCode");
    let options = document.getElementById("optionsAreaCode");
    if(areaCode){
        search.style.display = "block";
        options.style.display = "block";
    }else{
        search.style.display = "none";
        options.style.display = "none";
    }
}

function searchAllAreaCodes(e){
    let text = e.value.toString();
    if(text.charAt(0) !== '+'){
        text = "+" + text;
    }
    let options = document.getElementsByClassName("optionAreaCode");
    for(let i = 0; i < options.length; i++){
        if(options[i].textContent.startsWith(text)){
            options[i].style.display = "block";
        }
        else{
            options[i].style.display = "none";
        }
    }
}

function setValueAreaCode(e){
    let valueSelect = document.getElementById("valueAreaCode");
    valueSelect.value = e.textContent;
    checkAreaCode(valueSelect);
    areaCodeValue = e.textContent;
    setFullPhone();
}

function setFullPhone(){
    document.getElementById("fullPhone").value = areaCodeValue + phoneValue;
}
