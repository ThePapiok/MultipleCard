let disable = true;
let previousVerification = "";
const regVerificationNumber = new RegExp("^[0-9]{3} [0-9]{3}$");

function addWhiteSpace(e, input, length) {
    let previous = previousVerification;
    if (length === 3) {
        if (previous.charAt(previous.length - 1) === " ") {
            e.value = input.substring(0, length - 1);
        } else {
            e.value += " ";
        }
        input = e.value;
    }
    previousVerification = input;
}

function atStart() {
    const urlParams =new URLSearchParams(window.location.search);
    if(urlParams.has("error")){
        enableInputs();
    }
    checkLanguage();
}


function enableInputs(){
    if(disable){
        disable = false;
        let inputs = document.getElementsByTagName("input");
        for(let i = 0; i < inputs.length; i++){
            inputs[i].disabled = false;
        }
    }
}


function getCode(phone, param) {
    enableInputs();
    fetch("/get_verification_number", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "callingCode": "",
            "phone": phone,
            "param": param
        })
    })
        .then(response => response.text())
        .then(response => {
            if(response !== "ok"){
                document.getElementById("error").textContent = response;
            }
        })
        .catch(error => {
            console.error(error);
        });
}

