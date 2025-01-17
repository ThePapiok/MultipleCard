class ProductInfo {
    constructor(productId, hasPromotion) {
        this.productId = productId;
        this.hasPromotion = hasPromotion;
    }

    toString() {
        return JSON.stringify(this);
    }
}

let productsQuantity = 0;
let productsId = new Map();


function addProductId(id, e, hasPromotion, product, related, isCart) {
    let quantity = parseInt(e.textContent);
    let newValue;
    let result;
    let productInfo = new ProductInfo(id, hasPromotion).toString();
    let innerHtml;
    let price;
    if (productsQuantity < 100) {
        if (related) {
            if (quantity < (10 - parseInt(document.getElementById("product" + id).getElementsByClassName("quantity")[0].textContent))) {
                quantity++;
                e.textContent = quantity.toString();
                if (!productsId.has(productInfo)) {
                    productsId.set(productInfo, 1);
                } else {
                    productsId.set(productInfo, productsId.get(productInfo) + 1);
                }
                return true;
            }
            return false;
        }
        if (quantity < 10) {
            if (hasPromotion) {
                let count = document.getElementById("count" + id);
                if (count.dataset.actualValue !== "0") {
                    quantity++;
                    e.textContent = quantity.toString();
                    newValue = (parseInt(count.dataset.actualValue) - 1).toString();
                    count.dataset.actualValue = newValue;
                    count.textContent = newValue + ' ' + document.getElementById("textLeftProducts").textContent;
                } else {
                    product.style.pointerEvents = "none";
                    product.style.opacity = "40%";
                    result = document.createElement("div")
                    result.className = "result related";
                    price = product.getElementsByClassName("realPrice")[0].textContent;
                    result.dataset.price = (parseInt(price.substring(0, price.length - 2)) * 100).toString();
                    innerHtml = `<div class="result-vertical">
                    <div class="result-horizontal notImageContainer">
                        <span class="name">` + product.getElementsByClassName("name")[0].textContent + `</span>
                        <span class="quantity">0</span>
                        <a class="resultIcons" title="` + document.getElementById("textAddProduct").textContent + `"
                           onclick="addProduct('` + id + `', this.previousElementSibling, false, this.parentElement.parentElement.parentElement, true, ` + isCart + `)">
                            <img src="/images/plus.png" alt="add">
                        </a>
                        <a class="resultIcons" title="` + document.getElementById("textDeleteProduct").textContent + `"
                           onclick="deleteProduct('` + id + `', this.previousElementSibling.previousElementSibling, false, this.parentElement.parentElement.parentElement, true)">
                            <img src="/images/minus.png" alt="minus">
                        </a>
                `;
                    if (isCart) {
                        innerHtml += `
                    <a class="resultIcons" title="` + document.getElementById("textDelete").textContent + `" onclick="removeProduct('` + id + `' , this.parentElement.parentElement.parentElement,` + false + `)">
                            <img src="/images/close.png" alt="close">
                        </a>
                    `;
                    }
                    innerHtml += `
                </div>
                    <div class="result-horizontal imageContainer">
                        <div class="shopLogo" title="` + product.getElementsByClassName("shopLogo")[0].title + `">
                            <img class="shopImage" src="` + product.getElementsByClassName("shopImage")[0].src + `">
                        </div>
                        <img class="productImage" src="` + product.getElementsByClassName("productImage")[0].src + `" alt="product" onmouseenter="hideLogo(this.previousElementSibling)" onmouseleave="showLogo(this.previousElementSibling)">
                        <div class="description">` + product.getElementsByClassName("description")[0].textContent + `
                        </div>
                    </div>
                    <div class='result-horizontal notImageContainer'>
                        <span class='price fullPrice'>` + price + `</span>
                    </div>
                </div>`;
                    result.innerHTML = innerHtml;
                    product.after(result);
                    return false;
                }
            } else {
                quantity++;
                e.textContent = quantity.toString();
                if (product.getElementsByClassName("promotionContainer")[0] != null) {
                    productInfo = new ProductInfo(id, true).toString();
                }
            }
            if (!productsId.has(productInfo)) {
                productsId.set(productInfo, 1);
            } else {
                productsId.set(productInfo, productsId.get(productInfo) + 1);
            }
            return true;

        }
    }
    return false;
}

function deleteProductId(id, e, hasPromotion, product, related) {
    let quantity = parseInt(e.textContent);
    let size;
    let newValue;
    let productInfo = new ProductInfo(id, hasPromotion).toString();
    if (related && quantity === 0) {
        let result = document.getElementById("product" + id)
        result.style.pointerEvents = "auto";
        result.style.opacity = "100%";
        product.remove();
        return false;
    }
    if (quantity > 0) {
        quantity--;
        e.textContent = quantity.toString();
        if (hasPromotion) {
            let count = document.getElementById("count" + id);
            if (quantity < parseInt(count.dataset.startValue)) {
                newValue = (parseInt(count.dataset.actualValue) + 1).toString();
                count.dataset.actualValue = newValue;
                count.textContent = newValue + ' ' + document.getElementById("textLeftProducts").textContent;
            }
        } else {
            if (product.getElementsByClassName("promotionContainer")[0] != null) {
                productInfo = new ProductInfo(id, true).toString();
            }
        }
        if (productsId.has(productInfo)) {
            size = productsId.get(productInfo) - 1;
            if (size === 0) {
                productsId.delete(productInfo);
            } else {
                productsId.set(productInfo, size);
            }
        }
        return true;
    }
    return false;
}