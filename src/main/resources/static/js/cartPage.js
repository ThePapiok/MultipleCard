

class Product{
    constructor({isActive, productId, productName, description, productImageUrl, barcode, amount, shopId, startAtPromotion, expiredAtPromotion, countPromotion, amountPromotion, shopName, shopImageUrl}) {
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

function atStart(page) {
    let productKeys = [];
    let results = document.getElementById("results");
    let result;
    let product;
    let productsSession;
    let maxPage;
    checkLanguage();
    productsSession = sessionStorage.getItem("productsId");
    if (productsSession == null) {
        document.getElementById("buyButton").className = "grayButton";
        return;
    }
    document.getElementById("noResults").dataset.resultsEmpty = "false";
    productsId = new Map(Object.entries(JSON.parse(productsSession)));
    productsId.forEach((value, key) => productKeys.push(key));
    maxPage = Math.ceil(productKeys.length / 12.0);
    fetch("/get_products", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "productsId": productKeys,
            "page": page
        })
    })
        .then(response => response.json())
        .then(response => {
            for (let i = 0; i < response.length; i++) {
                result = document.createElement("div")
                result.className = "result";
                product = new Product(response[i]);
                let innerHtml = `<div class="result-vertical">
                    <div class="result-horizontal notImageContainer">
                        <span class="name">` + product.productName + `</span>
                        <span class="amount">` + productsId.get(product.productId) + `</span>
                        <a class="resultIcons"
                           onclick="addProduct('` + product.productId + `', this.previousElementSibling, this.parentElement.parentElement.parentElement)">
                            <img src="/images/plus.png" alt="add">
                        </a>
                        <a class="resultIcons"
                           onclick="deleteProduct('` + product.productId + `', this.previousElementSibling.previousElementSibling, this.parentElement.parentElement.parentElement)">
                            <img src="/images/minus.png" alt="minus">
                        </a>
                        <a class="resultIcons" onclick="removeProduct('` + product.productId + `' , this.parentElement.parentElement.parentElement)">
                            <img src="/images/close.png" alt="close">
                        </a>
                    </div>
                    <div class="result-horizontal imageContainer">
                        <div class="shopLogo" title="` + product.shopName + `">
                            <img class="shopImage" src="` + product.shopImageUrl + `">
                        </div>
                        <img id="productImage" src="` + product.productImageUrl + `" alt="product" onmouseenter="hideLogo(this.previousElementSibling)" onmouseleave="showLogo(this.previousElementSibling)">
                        <div class="description">` + product.description + `
                        </div>
                    </div>
                    <div class='result-horizontal notImageContainer price'>
                    `;
                if(product.startAtPromotion == null){
                    innerHtml += "<span class='amount fullAmount'>" + product.amount / 100 + "zł</span></div></div>";
                }
                else{
                    innerHtml += `
                         <div class="promotionContainer">
                            <div class="result-horizontal">
                                <div class="result-vertical promotionInfo">
                                    <div class="date">
                                        <span>` + product.startAtPromotion + `</span>
                                        <span>-</span>
                                        <span>` + product.expiredAtPromotion + `</span>
                                    </div>`;
                     if(!product.countPromotion === 0){
                         innerHtml += "<span className='productsLeft'>" + product.countPromotion + ' ' + document.getElementById("textLeftProducts") + "</span>";
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
            let newPage;
            let indexPage;
            let pagesSelect = document.getElementById("pagesSelect");
            fetch("/get_pages", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams({
                    "page": page,
                    "maxPage": maxPage,
                })
            })
                .then(response => response.json())
                .then(response => {
                    for (let i = 0; i < response.length; i++) {
                        indexPage = (i + 1).toString();
                        newPage = document.createElement("span");
                        newPage.className = "numberPage"
                        if (i === parseInt(page)) {
                            newPage.classList.add("selectedPage");
                        }
                        newPage.id = "page" + indexPage
                        newPage.textContent = indexPage;
                        newPage.onclick = () => setPage(maxPage, page);
                        pagesSelect.appendChild(newPage);
                    }
                    document.getElementById("nextPage").onclick = () => nextPage(maxPage);
                    checkButtonPages((parseInt(page) + 1).toString(), maxPage.toString());
                })
                .catch((error) => {
                    console.error(error);
                });

        })
        .catch((error) => {
            console.error(error);
        });
}


function addProduct(id, amount, e) {
    if (addProductId(id, amount)) {
        e.style.opacity = "100%";
        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
    }
}


function deleteProduct(id, amount, e) {
    if (deleteProductId(id, amount)) {
        sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
        if (amount.textContent === "0") {
            e.style.opacity = "40%";
        }
    }
}

function removeProduct(id, e) {
    productsId.delete(id);
    e.remove();
    sessionStorage.setItem("productsId", JSON.stringify(Object.fromEntries(productsId)));
    location.reload();
}

function buyProducts(){
    const cardId = sessionStorage.getItem("cardId");
    if (productsId.size === 0){
        return;
    }
    fetch("/buy_products?cardId=" + cardId, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(Object.fromEntries(productsId))
    })
        .then(content => {
            if (content.status === 200){
                window.location = "/cards?id=" + cardId + "&success";
            }
            else {
                window.location = "/cards?id=" + cardId + "&error";
            }
        })
        .catch(error => {
            console.error(error);
        });
}