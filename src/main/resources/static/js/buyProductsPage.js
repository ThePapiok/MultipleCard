function addProduct(id, e, hasPromotion, product, related, isCart) {
    if (addProductId(id, e, hasPromotion, product, related, isCart)) {
        let basketAmount = document.getElementById("basketAmount");
        basketAmount.dataset.empty = "false";
        basketAmount.textContent = (parseInt(basketAmount.textContent) + 1).toString();
    }
}

function deleteProduct(id, e, hasPromotion, product, related) {
    if (deleteProductId(id, e, hasPromotion, product, related)) {
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

function showCategories(e) {
    e.hidden = true;
}

function hideCategories(e) {
    e.hidden = false;
}

function showShopNames(e) {
    e.hidden = true;
}

function hideShopNames(e) {
    e.hidden = false;
}

function getCategories(categories, e) {
    let category;
    fetch("/get_categories", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "prefix": e.value
        })
    })
        .then(content => content.json())
        .then(content => {
            categories.innerHTML = "";
            for (let i = 0; i < content.length; i++) {
                category = document.createElement("li");
                category.className = "option";
                category.textContent = content[i];
                category.onclick = () => {
                    document.getElementById("categoryInput").value = content[i];
                    showOrHideCategories(categories);
                };
                categories.appendChild(category);
            }
        })
        .catch(error => {
            console.error(error);
        })
}

function getShopNames(shopNames, e) {
    let shopName;
    fetch("/get_shop_names", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "prefix": e.value
        })
    })
        .then(content => content.json())
        .then(content => {
            shopNames.innerHTML = "";
            for (let i = 0; i < content.length; i++) {
                shopName = document.createElement("li");
                shopName.className = "option";
                shopName.textContent = content[i];
                shopName.onclick = () => {
                    document.getElementById("shopNameInput").value = content[i];
                    showOrHideShopNames(shopNames);
                };
                shopNames.appendChild(shopName);
            }
        })
        .catch(error => {
            console.error(error);
        })
}

