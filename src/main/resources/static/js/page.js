function checkLanguage() {
    const is = sessionStorage.getItem("isEn");
    if (is != null && is === 'true') {
        document.getElementById("enLanguage").style.display = "none";
        document.getElementById("plLanguage").style.display = "inline";
    } else {
        document.getElementById("enLanguage").style.display = "inline";
        document.getElementById("plLanguage").style.display = "none";
    }
}

function setLanguage() {
    const urlParams = new URLSearchParams(window.location.search);
    const is = sessionStorage.getItem("isEn");
    if (is != null) {
        if (is === 'true') {
            sessionStorage.setItem('isEn', 'false');
            urlParams.set("lang", "pl");
        } else {
            sessionStorage.setItem('isEn', 'true');
            urlParams.set("lang", "eng");
        }
    } else {
        sessionStorage.setItem('isEn', "true");
        urlParams.set("lang", "eng");
    }
    window.location.search = urlParams;
}
