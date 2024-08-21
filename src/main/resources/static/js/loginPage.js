let previous = [false, false];
let ok = [false, false];
let success = false;

const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
const regPhone = new RegExp("^\\+([0-9]{1,4} [0-9]{3} [0-9]{3} [0-9]{3}|[0-9]*)$")

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
    const input = e.value;
    check(1, (input.length >= 11 && input.length <= 17 && regPhone.test(input)));
}

function  checkPassword(e){
    const input = e.value;
    check(2, (input.length >= 6 && input.length <= 25 && regPassword.test(input)));
}

function activeButton(){
    if(ok[0] && ok[1]){
        let button = document.getElementById("loginButton");
        button.className = "greenButton";
        button.type = "submit";
        success = true;
    }
}

function disableButton(){
    if((!ok[0] || !ok[1]) && success){
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
    let inputs = document.getElementsByTagName("input");
    checkPhone(inputs[0]);
}
