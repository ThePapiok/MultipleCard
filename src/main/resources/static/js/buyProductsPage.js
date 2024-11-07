let productsId = [];

function addProduct(id, e) {
    let amount = parseInt(e.textContent);
    if (amount < 10) {
        amount++;
        e.textContent = amount.toString();
        productsId.push(id);
        let basketAmount = document.getElementById("basketAmount");
        basketAmount.dataset.empty = "false";
        basketAmount.textContent = (parseInt(basketAmount.textContent) + 1).toString();
    }
}

function deleteProduct(id, e) {
    let amount = parseInt(e.textContent);
    if (amount > 0) {
        amount--;
        e.textContent = amount.toString();
        productsId.splice(productsId.indexOf(id), 1);
        let basketAmount = document.getElementById("basketAmount");
        basketAmount.textContent = (parseInt(basketAmount.textContent) - 1).toString();
        if (basketAmount.textContent === "0") {
            basketAmount.dataset.empty = "true";
        }
    }
}

function goToBasket(id) {
    sessionStorage.setItem("productsId", JSON.stringify(productsId));
    sessionStorage.setItem("cardId", id);
    window.location = "/cart";
}

function resetBasket() {
    sessionStorage.removeItem("productsId");
    sessionStorage.removeItem("cardId");
}