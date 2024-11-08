function addProduct(id, e) {
    if (addProductId(id, e)) {
        let basketAmount = document.getElementById("basketAmount");
        basketAmount.dataset.empty = "false";
        basketAmount.textContent = (parseInt(basketAmount.textContent) + 1).toString();
    }
}

function deleteProduct(id, e) {
    if (deleteProductId(id, e)) {
        let basketAmount = document.getElementById("basketAmount");
        basketAmount.textContent = (parseInt(basketAmount.textContent) - 1).toString();
        if (basketAmount.textContent === "0") {
            basketAmount.dataset.empty = "true";
        }
    }
}

function goToBasket(id) {
    sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
    sessionStorage.setItem("cardId", id);
    window.location = "/cart";
}

function resetBasket() {
    sessionStorage.removeItem("productsId");
    sessionStorage.removeItem("cardId");
}