let disable = true;

function atStart(isCard) {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("error")) {
        enableInputs();
    }
    checkLanguage();
    if (isCard) {
        checkCardName(document.getElementById('name'));
    }
}

function enableInputs() {
    if (disable) {
        disable = false;
        let inputs = document.getElementsByTagName("input");
        for (let i = 0; i < inputs.length; i++) {
            inputs[i].disabled = false;
        }
    }
}
