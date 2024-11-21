class Product {
    constructor({
                    isActive,
                    productId,
                    productName,
                    description,
                    productImageUrl,
                    barcode,
                    amount,
                    shopId,
                    startAtPromotion,
                    expiredAtPromotion,
                    countPromotion,
                    amountPromotion,
                    shopName,
                    shopImageUrl
                }) {
        this.isActive = isActive;
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.productImageUrl = productImageUrl;
        this.barcode = barcode;
        this.amount = amount;
        this.shopId = shopId;
        this.startAtPromotion = startAtPromotion;
        this.expiredAtPromotion = expiredAtPromotion;
        this.countPromotion = countPromotion;
        this.amountPromotion = amountPromotion;
        this.shopName = shopName;
        this.shopImageUrl = shopImageUrl;
    }
}

let totalAmount = 0;

function atStart(page) {
    let productKeys = [];
    let results = document.getElementById("results");
    let result;
    let product;
    let productsSession;
    let hasPromotion;
    let amount;
    let countPromotion;
    let productId;
    let productIdWithoutPromotion;
    let productInfo;
    let incAmount;
    let related;
    let tooMany = false;
    checkLanguage();
    if (sessionStorage.getItem("newOrder") != null) {
        sessionStorage.removeItem("newOrder");
        document.getElementById("error").textContent = document.getElementById("textGetNewCount").textContent;
    }
    productsSession = sessionStorage.getItem("productsId");
    productsId = new Map(Object.entries(JSON.parse(productsSession)));
    productsId.forEach((value, key) => {
        productKeys.push(key);
        if (value > 10) {
            tooMany = true;
            return;
        }
        productsAmount += value;
    });
    if (productsAmount > 100 || tooMany) {
        document.getElementById("error").textContent = document.getElementById("textTooManyProducts").textContent;
        return;
    }
    fetch("/get_products", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(productKeys)
    })
        .then(response => response.json())
        .then(response => {
            if (response.length !== 0) {
                document.getElementById("noResults").dataset.resultsEmpty = "false";
                document.getElementById("buyButton").className = "greenButton";
            } else {
                document.getElementById("buyButton").className = "grayButton";
            }
            for (let i = 0; i < response.length; i++) {
                product = new Product(response[i]);
                productId = product.productId;
                result = document.createElement("div")
                result.id = "product" + productId;
                countPromotion = product.countPromotion;
                productInfo = new ProductInfo(productId, product.startAtPromotion != null).toString()
                amount = productsId.get(productInfo);
                if (countPromotion != null) {
                    if (countPromotion === 0) {
                        productsId.delete(productInfo);
                        productIdWithoutPromotion = new ProductInfo(productId, false).toString();
                        if (productsId.has(productIdWithoutPromotion)) {
                            productsId.set(productIdWithoutPromotion, productsId.get(productIdWithoutPromotion) + amount);
                        } else {
                            productsId.set(productIdWithoutPromotion, amount);
                        }
                        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
                        sessionStorage.setItem("newOrder", "1");
                        amount = countPromotion;
                        continue;
                    } else if (countPromotion < amount) {
                        incAmount = (amount - countPromotion);
                        productIdWithoutPromotion = new ProductInfo(productId, false).toString();
                        productsId.set(productInfo, productsId.get(productInfo) - incAmount);
                        if (productsId.has(productIdWithoutPromotion)) {
                            productsId.set(productIdWithoutPromotion, productsId.get(productIdWithoutPromotion) + incAmount);
                        } else {
                            productsId.set(productIdWithoutPromotion, incAmount);
                        }
                        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
                        sessionStorage.setItem("newOrder", "1");
                        amount = countPromotion;
                    } else if (((amount - countPromotion) === 0) && productsId.has(new ProductInfo(productId, false).toString())) {
                        result.style.pointerEvents = "none";
                        result.style.opacity = "40%";
                    }
                }
                hasPromotion = (countPromotion != null);
                if (!hasPromotion && product.startAtPromotion == null && productsId.has(new ProductInfo(productId, true).toString())) {
                    related = true;
                    hasPromotion = false;
                    result.className = "result related";
                } else {
                    result.className = "result";
                    related = false;
                }
                if (product.startAtPromotion == null) {
                    result.dataset.price = product.amount;
                } else {
                    result.dataset.price = product.amountPromotion;
                }
                totalAmount += (amount * result.dataset.price);
                let innerHtml = `<div class="result-vertical">
                    <div class="result-horizontal notImageContainer">
                        <span class="name">` + product.productName + `</span>
                        <span class="amount">` + amount + `</span>
                        <a class="resultIcons"
                           onclick="addProduct('` + productId + `', this.previousElementSibling, ` + hasPromotion + `, this.parentElement.parentElement.parentElement, ` + related + `, true)">
                            <img src="/images/plus.png" alt="add">
                        </a>
                        <a class="resultIcons"
                           onclick="deleteProduct('` + productId + `', this.previousElementSibling.previousElementSibling, ` + hasPromotion + `, this.parentElement.parentElement.parentElement, ` + related + `)">
                            <img src="/images/minus.png" alt="minus">
                        </a>
                        <a class="resultIcons" onclick="removeProduct('` + productId + `' , this.parentElement.parentElement.parentElement,` + (product.startAtPromotion != null) + `)">
                            <img src="/images/close.png" alt="close">
                        </a>
                    </div>
                    <div class="result-horizontal imageContainer">
                        <div class="shopLogo" title="` + product.shopName + `">
                            <img class="shopImage" src="` + product.shopImageUrl + `">
                        </div>
                        <img class="productImage" src="` + product.productImageUrl + `" alt="product" onmouseenter="hideLogo(this.previousElementSibling)" onmouseleave="showLogo(this.previousElementSibling)">
                        <div class="description">` + product.description + `
                        </div>
                    </div>
                    <div class='result-horizontal notImageContainer price'>
                    <span hidden class='realAmount'>` + product.amount / 100 + `zł</span>
                    `;
                if (product.startAtPromotion == null) {
                    innerHtml += "<span class='amount fullAmount'>" + product.amount / 100 + "zł</span></div></div>";
                } else {
                    innerHtml += `
                         <div class="promotionContainer">
                            <div class="result-horizontal">
                                <div class="result-vertical promotionInfo">
                                    <div class="date">
                                        <span>` + product.startAtPromotion + `</span>
                                        <span>-</span>
                                        <span>` + product.expiredAtPromotion + `</span>
                                    </div>`;
                    if (countPromotion != null) {
                        innerHtml += "<span class='productsLeft' data-actual-value='" + (countPromotion - amount) + "' data-start-value='" + countPromotion + "' id='" + "count" + productId + "'>" + (countPromotion - amount) + ' ' + document.getElementById("textLeftProducts").textContent + "</span>";
                    }
                    innerHtml += `</div>
                                <div class="promotionAmount">
                                    <span class="amount promotion">` + product.amount / 100 + `</span>
                                    <span class="amount">` + product.amountPromotion / 100 + 'zł' + `</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    </div>
                    `;
                }
                result.innerHTML = innerHtml;
                results.appendChild(result);
            }
            document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
            if (sessionStorage.getItem("newOrder") != null) {
                location.reload();
            }
        })
        .catch((error) => {
            console.error(error);
        });
}


function addProduct(id, e, hasPromotion, product, related, isCart) {
    if (addProductId(id, e, hasPromotion, product, related, isCart)) {
        productsAmount++;
        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
        totalAmount += parseInt(product.dataset.price);
        document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
    }
}


function deleteProduct(id, e, hasPromotion, product, related) {
    if (deleteProductId(id, e, hasPromotion, product, related)) {
        productsAmount--;
        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
        if (e.textContent === "0") {
            removeProduct(id, product, hasPromotion);
        }
        totalAmount -= parseInt(product.dataset.price);
        document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
    }
}

function removeProduct(productId, e, hasPromotion) {
    const productInfo = new ProductInfo(productId, hasPromotion).toString();
    let amount = productsId.get(productInfo);
    let parentProduct;
    if (amount == null) {
        amount = 0;
    }
    productsAmount -= amount;
    totalAmount -= (parseInt(e.dataset.price) * amount);
    document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
    productsId.delete(productInfo);
    if (productsId.size === 0) {
        document.getElementById("noResults").dataset.resultsEmpty = "true";
        document.getElementById("buyButton").className = "grayButton";
    }
    if (e.classList.contains("related")) {
        parentProduct = document.getElementById("product" + productId);
        parentProduct.style.pointerEvents = "auto";
        parentProduct.style.opacity = "100%";
    }
    e.remove();
    sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
}

function buyProducts(e) {
    const cardId = sessionStorage.getItem("cardId");
    if (productsId.size === 0 || e.className === "grayButton") {
        return;
    }
    fetch("/make_order?cardId=" + cardId, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(Object.fromEntries(productsId))
    })
        .then(content => content.text())
        .then(content => {
            window.location = content;
        })
        .catch(error => {
            console.error(error);
        });
}