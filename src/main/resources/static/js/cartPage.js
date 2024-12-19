let buyProd = false;


class Product {
    constructor({
                    isActive,
                    productId,
                    productName,
                    description,
                    productImageUrl,
                    barcode,
                    price,
                    shopId,
                    startAtPromotion,
                    expiredAtPromotion,
                    quantityPromotion,
                    newPricePromotion,
                    shopName,
                    shopImageUrl
                }) {
        this.isActive = isActive;
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.productImageUrl = productImageUrl;
        this.barcode = barcode;
        this.price = price;
        this.shopId = shopId;
        this.startAtPromotion = startAtPromotion;
        this.expiredAtPromotion = expiredAtPromotion;
        this.quantityPromotion = quantityPromotion;
        this.newPricePromotion = newPricePromotion;
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
    let quantity;
    let quantityPromotion;
    let productId;
    let productIdWithoutPromotion;
    let productInfo;
    let diffQuantity;
    let related;
    let tooMany = false;
    checkLanguage();
    if (sessionStorage.getItem("newOrder") != null) {
        sessionStorage.removeItem("newOrder");
        document.getElementById("error").textContent = document.getElementById("textGetNewQuantity").textContent;
    }
    productsSession = sessionStorage.getItem("productsId");
    productsId = new Map(Object.entries(JSON.parse(productsSession)));
    productsId.forEach((value, key) => {
        productKeys.push(key);
        if (value > 10) {
            tooMany = true;
            return;
        }
        productsQuantity += value;
    });
    if (productsQuantity > 100 || tooMany) {
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
            for (let i = 0; i < response.length; i++) {
                product = new Product(response[i]);
                productId = product.productId;
                result = document.createElement("div")
                result.id = "product" + productId;
                quantityPromotion = product.quantityPromotion;
                productInfo = new ProductInfo(productId, product.startAtPromotion != null).toString()
                quantity = productsId.get(productInfo);
                if (quantityPromotion != null) {
                    if (quantityPromotion === 0) {
                        productsId.delete(productInfo);
                        productIdWithoutPromotion = new ProductInfo(productId, false).toString();
                        if (productsId.has(productIdWithoutPromotion)) {
                            productsId.set(productIdWithoutPromotion, productsId.get(productIdWithoutPromotion) + quantity);
                        } else {
                            productsId.set(productIdWithoutPromotion, quantity);
                        }
                        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
                        sessionStorage.setItem("newOrder", "1");
                        quantity = quantityPromotion;
                        continue;
                    } else if (quantityPromotion < quantity) {
                        diffQuantity = (quantity - quantityPromotion);
                        productIdWithoutPromotion = new ProductInfo(productId, false).toString();
                        productsId.set(productInfo, productsId.get(productInfo) - diffQuantity);
                        if (productsId.has(productIdWithoutPromotion)) {
                            productsId.set(productIdWithoutPromotion, productsId.get(productIdWithoutPromotion) + diffQuantity);
                        } else {
                            productsId.set(productIdWithoutPromotion, diffQuantity);
                        }
                        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
                        sessionStorage.setItem("newOrder", "1");
                        quantity = quantityPromotion;
                    } else if (((quantity - quantityPromotion) === 0) && productsId.has(new ProductInfo(productId, false).toString())) {
                        result.style.pointerEvents = "none";
                        result.style.opacity = "40%";
                    }
                }
                hasPromotion = (quantityPromotion != null);
                if (!hasPromotion && product.startAtPromotion == null && productsId.has(new ProductInfo(productId, true).toString())) {
                    related = true;
                    hasPromotion = false;
                    result.className = "result related";
                } else {
                    result.className = "result";
                    related = false;
                }
                if (product.startAtPromotion == null) {
                    result.dataset.price = product.price;
                } else {
                    result.dataset.price = product.newPricePromotion;
                }
                totalAmount += (quantity * result.dataset.price);
                let innerHtml = `<div class="result-vertical">
                    <div class="result-horizontal notImageContainer">
                        <span class="name">` + product.productName + `</span>
                        <span class="quantity">` + quantity + `</span>
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
                    <div class='result-horizontal notImageContainer'>
                    <span hidden class='realPrice'>` + product.price / 100 + `zł</span>
                    `;
                if (product.startAtPromotion == null) {
                    innerHtml += "<span class='price fullPrice'>" + product.price / 100 + "zł</span></div></div>";
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
                    if (quantityPromotion != null) {
                        innerHtml += "<span class='productsLeft' data-actual-value='" + (quantityPromotion - quantity) + "' data-start-value='" + quantityPromotion + "' id='" + "count" + productId + "'>" + (quantityPromotion - quantity) + ' ' + document.getElementById("textLeftProducts").textContent + "</span>";
                    }
                    innerHtml += `</div>
                                <div class="promotionPrice">
                                    <span class="price promotion">` + product.price / 100 + `</span>
                                    <span class="price">` + product.newPricePromotion / 100 + 'zł' + `</span>
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
            if (response.length !== 0) {
                document.getElementById("noResults").dataset.resultsEmpty = "false";
                document.getElementById("buyButton").className = "greenButton";
                checkPoints();
            } else {
                document.getElementById("buyButton").className = "grayButton";
                document.getElementById("buyButtonPoints").className = "grayButton";
            }
            document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
            document.getElementById("totalPoints").textContent = Math.ceil(totalAmount / 100) + "p";
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
        productsQuantity++;
        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
        totalAmount += parseInt(product.dataset.price);
        checkPoints();
        document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
        document.getElementById("totalPoints").textContent = Math.ceil(totalAmount / 100) + "p";
    }
}


function deleteProduct(id, e, hasPromotion, product, related) {
    if (deleteProductId(id, e, hasPromotion, product, related)) {
        productsQuantity--;
        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
        if (e.textContent === "0") {
            removeProduct(id, product, hasPromotion);
        }
        totalAmount -= parseInt(product.dataset.price);
        checkPoints();
        document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
        document.getElementById("totalPoints").textContent = Math.ceil(totalAmount / 100) + "p";
    }
}

function removeProduct(productId, e, hasPromotion) {
    const productInfo = new ProductInfo(productId, hasPromotion).toString();
    let quantity = productsId.get(productInfo);
    let parentProduct;
    if (quantity == null) {
        quantity = 0;
    }
    productsQuantity -= quantity;
    totalAmount -= (parseInt(e.dataset.price) * quantity);
    checkPoints();
    document.getElementById("totalAmount").textContent = totalAmount / 100 + "zł";
    document.getElementById("totalPoints").textContent = Math.ceil(totalAmount / 100) + "p";
    productsId.delete(productInfo);
    if (productsId.size === 0) {
        document.getElementById("noResults").dataset.resultsEmpty = "true";
        document.getElementById("buyButton").className = "grayButton";
        document.getElementById("buyButtonPoints").className = "grayButton";
    }
    if (e.classList.contains("related")) {
        parentProduct = document.getElementById("product" + productId);
        parentProduct.style.pointerEvents = "auto";
        parentProduct.style.opacity = "100%";
    }
    e.remove();
    sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
}

function checkPoints() {
    const points = Math.ceil(totalAmount / 100);
    if (points <= parseInt(document.getElementById("points").textContent) && points !== 0) {
        document.getElementById("buyButtonPoints").className = "greenButton";
    } else {
        document.getElementById("buyButtonPoints").className = "grayButton";
    }
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
            if (content.includes("https://merch-prod.snd.payu.com")) {
                window.location = content;
            } else {
                document.getElementById("error").textContent = content;
            }
        })
        .catch(error => {
            console.error(error);
        });
}

function buyProductsByPoints(e) {
    const cardId = sessionStorage.getItem("cardId");
    if (productsId.size === 0 || e.className === "grayButton") {
        return;
    }
    fetch("/buy_for_points?cardId=" + cardId, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(Object.fromEntries(productsId))
    })
        .then(content => content.text())
        .then(content => {
            if (content === "ok") {
                window.location = "/?success";
            } else {
                document.getElementById("error").textContent = content;
            }
        })
        .catch(error => {
            console.error(error);
        });
}

function showOrHideBuyProducts(e) {
    if (!buyProd && (productsId.size === 0 || document.getElementById("buyButtonPoints").className === "grayButton")) {
        return;
    }
    let buyProduct = document.getElementById("buy");
    buyProd = !buyProd;
    buyProduct.hidden = !buyProd;
    if (buyProd) {
        const cord = e.getBoundingClientRect();
        buyProduct.style.left = cord.right + window.scrollX + 'px';
        buyProduct.style.top = cord.top + window.scrollY + 'px';
    }
}

