let previousVerification = ["", ""];
let ok = [false, false];
let previous = [false, false];
let success = false;
const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");


function checkVerificationEmail(e){
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 0, input, length);
    check(1,  length === 7 && regVerificationNumber.test(input));
}

function checkVerificationSms(e){
    let input = e.value;
    let length = input.length;
    addWhiteSpace(e, 1, input, length);
    check(2,  length === 7 && regVerificationNumber.test(input));
}

function addWhiteSpace(e, index, input, length){
    let previous = previousVerification[index];
    if(length === 3){
        if(previous.charAt(previous.length-1) === " ")
        {
            e.value = input.substring(0,length-1);
        }
        else{
            e.value+=" ";
        }
        input = e.value;
    }
    previousVerification[index] = input;
}

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

function activeButton(){
    if(ok[0] && ok[1]){
        let button = document.getElementById("registerButton");
        button.className = "greenButton";
        button.type = "submit";
        success = true;
    }
}

function disableButton(){
    if((!ok[0] || !ok[1]) && success){
        let button = document.getElementById("registerButton");
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
    validation.style.top = cord.top + window.scrollY + 50  + 'px';
}

function hideValidation(e) {
    e.parentElement.nextElementSibling.style.display = "none";
}


