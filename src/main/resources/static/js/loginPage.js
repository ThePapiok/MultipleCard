let previousLogin = false;
let previousPassword = false;
let okLogin= false;
let okPassword= false;
let x;
let y;
const regPassword = new RegExp("(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*])");
document.addEventListener("mousemove", getCoordinates);



function checkLogin(){
    var input = document.getElementById("loginInput").value;
    if(input.length === 24 || input.length === 9){
        okLogin = true;
        if(previousLogin === false){
            document.getElementById("close1").style.display = "none";
            document.getElementById("check1").style.display = "inline";
        }
        activeButton();
        previousLogin = true;
    }
    else{
        okLogin = false;
        if(previousLogin === true){
            document.getElementById("close1").style.display = "inline";
            document.getElementById("check1").style.display = "none";
        }
        disableButton();
        previousLogin = false;
    }

}

function  checkPassword(){
    var input = document.getElementById("passwordInput").value;
    if((input.length >= 6 || input.contains) && regPassword.test(input)) {
        okPassword = true;
        if(previousPassword === false){
            document.getElementById("close2").style.display = "none";
            document.getElementById("check2").style.display = "inline";
        }
        activeButton();
        previousPassword = true;
    }
    else{
        okPassword = false;
        if(previousPassword === true){
            document.getElementById("close2").style.display = "inline";
            document.getElementById("check2").style.display = "none";
        }
        disableButton();
        previousPassword = false;

    }
}

function activeButton(){
    if(okLogin && okPassword && (previousLogin === false || previousPassword === false)){
        var button = document.getElementById("login");
        button.className = "greenButton";
        button.type = "submit";
    }
}

function disableButton(){
    if(((!okLogin && previousLogin) && (okPassword)) || ((!okPassword && previousPassword) && (okLogin))){
        var button = document.getElementById("login");
        button.className = "grayButton";
        button.type = "button";
    }
}

function showValidationLogin(){
    var info = document.getElementById("validationLogin");
    changeCoordinates(info);
}

function hideValidationLogin() {
    document.getElementById("validationLogin").style.display = "none";
}

function showValidationPassword(){
    var info = document.getElementById("validationPassword");
    changeCoordinates(info);

}

function hideValidationPassword(){
    document.getElementById("validationPassword").style.display = "none";
}

function changeCoordinates(e){
    e.style.display = "inline";
    e.style.left = x + 'px';
    e.style.top = y-150 + 'px';
}

function getCoordinates(e){
    x=e.screenX;
    y=e.screenY;
}
