let pin = "";

function atStart() {
    checkLanguage();
}

function addNum(num) {
    if (pin.length < 4) {
        pin += num;
        document.getElementById("char" + pin.length).firstElementChild.textContent = "*";
    }
    if (pin.length === 4) {
        document.getElementById("charSubmit").className = "char charOk";
    }
}

function delNum() {
    if (pin.length > 0) {
        document.getElementById("char" + pin.length).firstElementChild.textContent = "";
        pin = pin.substring(0, pin.length - 1);
        document.getElementById("charSubmit").className = "char charBad";
    }
}

function submitPin() {
    if (pin.length === 4) {
        fetch("/check_pin", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "pin": pin
            })
        })
            .then(response => response.text())
            .then(response => {
                window.location = response;
            })
            .catch(error => {
                console.error(error);
            })
    }
}