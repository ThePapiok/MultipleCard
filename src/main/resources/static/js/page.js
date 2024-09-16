function checkLanguage() {
    const is = sessionStorage.getItem("isEn");
    if (is !== null && is === 'true') {
        document.getElementById("enLanguage").style.display = "none";
        document.getElementById("plLanguage").style.display = "inline";
    } else {
        console.log("xd");
        document.getElementById("enLanguage").style.display = "inline";
        document.getElementById("plLanguage").style.display = "none";
    }
}

function setLanguage() {
    const is = sessionStorage.getItem("isEn");
    if (is !== null) {
        if (is === 'true') {
            sessionStorage.setItem('isEn', 'false');
        } else {
            sessionStorage.setItem('isEn', 'true');
        }
    } else {
        sessionStorage.setItem('isEn', "true");
    }
}

