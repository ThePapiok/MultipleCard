function atStart() {
    checkLanguage();
}

function goToProducts() {
    let params = new URLSearchParams(window.location.search);
    params.set("products", "true");
    window.location.search = params;
}

function goToBuy() {
    let params = new URLSearchParams(window.location.search);
    params.set("buy", "true");
    window.location.search = params;
}