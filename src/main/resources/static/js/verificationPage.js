let previousVerificationNumber = "";
let ok = false;
let previous = false;
const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");

function checkVerificationNumber(e){
    let input = e.value;
    let length = input.length;
    if(length === 3){
        console.log(previousVerificationNumber.charAt(previousVerificationNumber.length-1));
        if(previousVerificationNumber.charAt(previousVerificationNumber.length-1) === " ")
        {
            console.log("xd");
            e.value = input.substring(0,length-1);
        }
        else{
            e.value+=" ";
        }
        input = e.value;
    }
    previousVerificationNumber = input;
    if(length === 7 && regVerificationNumber.test(input)){
        ok = true;
        if(previous === false){
            document.getElementById("close1").style.display = "none";
            document.getElementById("check1").style.display = "inline";
        }
        if(ok !== previous)
        {
            let button = document.getElementById("registerButton");
            button.className = "greenButton";
            button.type = "submit";
            previous = true;
        }
    }
    else{
        ok = false;
        if(previous === true){
            document.getElementById("close1").style.display = "inline";
            document.getElementById("check1").style.display = "none";
        }
        if(ok !== previous)
        {
            let button = document.getElementById("registerButton");
            button.className = "grayButton";
            button.type = "button";
            previous = false;
        }
    }
}

function showValidation(e){
    let info = document.getElementById("validationVerificationNumber");
    const cord = e.getBoundingClientRect();
    info.style.display = "inline";
    info.style.left = cord.right + window.scrollX + 'px';
    info.style.top = cord.top + window.scrollY  + 'px';
}

function hideValidation() {
    document.getElementById("validationVerificationNumber").style.display = "none";
}


