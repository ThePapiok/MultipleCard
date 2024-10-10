let disable = true;

function atStart() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("error")) {
        enableInputs();
    }
    checkLanguage();
}

function enableInputs() {
    console.log("xd");
    if (disable) {
        disable = false;
        let inputs = document.getElementsByTagName("input");
        for (let i = 0; i < inputs.length; i++) {
            inputs[i].disabled = false;
        }
    }
}



