let previous = [false, false, false];
let ok = [false, false, false];
const buttonId = 'loginButton';

function atStart() {
    checkCallingCode(document.getElementById("valueCallingCode"), 1);
    document.getElementById("searchCallingCode").value = "";
    document.getElementById("fullPhone").value = document.getElementById("valueCallingCode").value + document.getElementById("phone").value;
    checkLanguage();
}
