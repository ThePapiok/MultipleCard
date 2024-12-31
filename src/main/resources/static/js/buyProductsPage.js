function addProduct(id, e, hasPromotion, product, related, isCart) {
    if (addProductId(id, e, hasPromotion, product, related, isCart)) {
        let basketQuantity = document.getElementById("basketQuantity");
        basketQuantity.dataset.empty = "false";
        basketQuantity.textContent = (parseInt(basketQuantity.textContent) + 1).toString();
        productsQuantity++;
    }
}

function deleteProduct(id, e, hasPromotion, product, related) {
    if (deleteProductId(id, e, hasPromotion, product, related)) {
        let basketQuantity = document.getElementById("basketQuantity");
        basketQuantity.textContent = (parseInt(basketQuantity.textContent) - 1).toString();
        productsQuantity--;
        if (basketQuantity.textContent === "0") {
            basketQuantity.dataset.empty = "true";
        }
    }
}

function goToBasket(id) {
    sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
    sessionStorage.setItem("cardId", id);
    window.location = "/cart";
}

