let deleteProd = false;
let blockProd = false;

function setParams() {
    const value = parseInt(document.getElementById("searchSelect").value);
    const text = document.getElementById("searchInput").value;
    let sort = null;
    let url = "/products";
    switch (value) {
        case 1:
            sort = "isDescending=false";
            break;
        case 2:
            sort = "field=date";
            break;
        case 3:
            sort = "field=date&isDescending=false";
            break;
    }
    if (sort != null) {
        url += ("?" + sort);
    }
    if (text !== "") {
        if (sort == null) {
            url += "?";
        } else {
            url += "&";
        }
        url += ("text=" + text);
    }
    window.location = url;
}

function atStart(page, isDescending, field, maxPage) {
    checkLanguage();
    let selectedPage = document.getElementById("page" + page);
    if (selectedPage != null) {
        selectedPage.classList.add("selectedPage");
    }
    let searchSelect = document.getElementById("searchSelect");
    if (isDescending === "true") {
        switch (field) {
            case "date":
                searchSelect.value = 2;
                break;
        }
    } else {
        switch (field) {
            case "count":
                searchSelect.value = 1;
                break;
            case "date":
                searchSelect.value = 3;
                break;
        }
    }
    let prices = document.getElementsByClassName("price");
    let price;
    let noPromotionContainer;
    let promotionContainer;
    let promotion
    let amount;
    let count;
    let resultVertical;
    if (promotions != null) {
        for (let i = 0; i < prices.length; i++) {
            price = prices[i];
            for (let j = 0; j < promotions.length; j++) {
                promotion = promotions[j];
                if (promotion.productId === price.dataset.id) {
                    amount = promotion.amount;
                    count = promotion.count;
                    price.parentElement.firstElementChild.firstElementChild.nextElementSibling.nextElementSibling.firstElementChild.src = "/images/discount_fill.png";
                    noPromotionContainer = price.firstElementChild;
                    noPromotionContainer.hidden = true;
                    promotionContainer = noPromotionContainer.nextElementSibling;
                    promotionContainer.hidden = false;
                    resultVertical = promotionContainer.firstElementChild.firstElementChild;
                    if (count !== 0) {
                        resultVertical.firstElementChild.nextElementSibling.textContent = promotion.count + ' ' + document.getElementById("textLeftProducts").textContent;
                    }
                    resultVertical.nextElementSibling.firstElementChild.nextElementSibling.textContent = amount / 100 + "zł";
                    resultVertical.firstElementChild.firstElementChild.textContent = promotion.startAt;
                    resultVertical.firstElementChild.firstElementChild.nextElementSibling.nextElementSibling.textContent = promotion.expiredAt;
                    promotions.splice(j, 1);
                }
            }
        }
    }
    checkButtonPages(page, maxPage);
}

function showOrHideDeleteProduct(productId, e) {
    let deleteProduct = document.getElementById("delete");
    deleteProd = !deleteProd;
    deleteProduct.hidden = !deleteProd;
    if (deleteProd) {
        const cord = e.getBoundingClientRect();
        deleteProduct.style.left = cord.right + window.scrollX + 'px';
        deleteProduct.style.top = cord.top + window.scrollY + 'px';
        deleteProduct.dataset.id = productId;
    } else {
        deleteProduct.dataset.id = "";
    }
}

function showOrHideBlockProduct(productId, e) {
    let blockProduct = document.getElementById("block");
    blockProd = !blockProd;
    blockProduct.hidden = !blockProd;
    if (blockProd) {
        const cord = e.getBoundingClientRect();
        blockProduct.style.left = cord.right + window.scrollX + 'px';
        blockProduct.style.top = cord.top + window.scrollY + 'px';
        blockProduct.dataset.id = productId;
    } else {
        blockProduct.dataset.id = "";
    }
}

function deleteProduct() {
    fetch("/products", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": document.getElementById("delete").dataset.id
        })
    }).then(response => response.text())
        .then(response => {
            if (response === "ok") {
                window.location.href = "/products";
            } else {
                showOrHideDeleteProduct();
                document.getElementById("error").textContent = response;
            }
        }).catch((error) => {
        console.error(error);
    });
}

function blockProduct() {
    fetch("/block_product", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": document.getElementById("block").dataset.id
        })
    }).then(response => response.text())
        .then(response => {
            if (response === "ok") {
                window.location.href = "/products";
            } else {
                showOrHideBlockProduct();
                document.getElementById("error").textContent = response;
            }
        }).catch((error) => {
        console.error(error);
    });
}

function unblockProduct(productId) {
    fetch("/unblock_product", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": productId
        })
    }).then(response => response.text())
        .then(response => {
            if (response === "ok") {
                window.location.href = "/products";
            } else {
                document.getElementById("error").textContent = response;
            }
        }).catch((error) => {
        console.error(error);
    });
}