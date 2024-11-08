let maxPage;

class Promotion {
    constructor(productId, startAt, expiredAt, count, amount) {
        this.productId = productId;
        this.startAt = startAt;
        this.expiredAt = expiredAt;
        this.count = count;
        this.amount = amount;
    }
}

function atStart(page) {
    let productKeys = [];
    let results = document.getElementById("results");
    let result;
    let product;
    let promotion;
    let productsSession;
    let promotions = [];
    checkLanguage();
    productsSession = sessionStorage.getItem("productsId");
    if (productsSession == null) {
        return;
    }
    document.getElementById("noResults").dataset.resultsSize = "1";
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
                product = response[i];
                promotion = product.promotion;
                if (promotion != null) {
                    promotions.push(new Promotion(product.id, promotion.startAt, promotion.expiredAt, promotion.count, promotion.amount));
                }
                result.innerHTML = `<div class="result-vertical">
                    <div class="result-horizontal notImageContainer">
                        <span class="name">` + product.name + `</span>
                        <span class="amount">` + productsId.get(product.id) + `</span>
                        <a class="resultIcons"
                           onclick="addProduct('` + product.id + `', this.previousElementSibling, this.parentElement.parentElement.parentElement)">
                            <img src="/images/plus.png" alt="add">
                        </a>
                        <a class="resultIcons"
                           onclick="deleteProduct('` + product.id + `', this.previousElementSibling.previousElementSibling, this.parentElement.parentElement.parentElement)">
                            <img src="/images/minus.png" alt="minus">
                        </a>
                        <a class="resultIcons" onclick="removeProduct('` + product.id + `' , this.parentElement.parentElement.parentElement)">
                            <img src="/images/close.png" alt="close">
                        </a>
                    </div>
                    <div class="result-horizontal imageContainer">
                        <img id="productImage" src="` + product.imageUrl + `" alt="product">
                        <div class="description">` + product.description + `
                        </div>
                    </div>
                    <div class="result-horizontal notImageContainer price" data-id="` + product.id + `">
                        <span class="amount fullAmount">` + product.amount / 100 + `zł</span>
                        <div hidden class="promotionContainer">
                            <div class="result-horizontal">
                                <div class="result-vertical promotionInfo">
                                    <div class="date">
                                        <span></span>
                                        <span>-</span>
                                        <span></span>
                                    </div>
                                    <span class="productsLeft"></span>
                                </div>
                                <div class="promotionAmount">
                                    <span class="amount promotion">` + product.amount / 100 + `</span>
                                    <span class="amount"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>`
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
                    setPromotions(promotions, false);
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