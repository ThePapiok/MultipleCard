let okBarcode = false;
let okAmount = false;
let productsBought = new Map();
let productsAvailable = new Map();
let productsInfo = new Map();
let maxAmount;
let barcodeProduct;
let index = 0;
let productsBarcode = new Map();
let productsVisible = [];

class Product {
    constructor(name, description, imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}

function atStart() {
    let product;
    let barcode;
    for (let i = 0; i < products.length; i++) {
        product = products[i];
        barcode = product.barcode;
        productsAvailable.set(product.id, barcode);
        if (!productsInfo.has(barcode)) {
            productsInfo.set(barcode, new Product(product.name, product.description, product.imageUrl));
        }
    }
    checkLanguage();
}

function checkBarcode(e) {
    const value = e.value;
    let searchButton = document.getElementById("searchButton");
    if (regBarcode.test(value) && value.length === 13) {
        searchButton.style.backgroundColor = "#839788";
        searchButton.style.cursor = "pointer";
        okBarcode = true;
    } else {
        searchButton.style.backgroundColor = "#CFCBCB";
        searchButton.style.cursor = "auto";
        okBarcode = false;
    }
}

function checkAmount(e) {
    const value = e.value;
    let amountButton = document.getElementById("buttonAmount");
    if (regBarcode.test(value) && value.length <= 4 && value.length >= 1 && barcodeProduct != null) {
        amountButton.style.backgroundColor = "#839788";
        amountButton.style.cursor = "pointer";
        okAmount = true;
    } else {
        amountButton.style.backgroundColor = "#CFCBCB";
        amountButton.style.cursor = "auto";
        okAmount = false;
    }
}

function searchProduct(e) {
    const barcode = e.value;
    let found = false;
    let imageUrl;
    let product;
    maxAmount = 0;
    if (okBarcode) {
        productsAvailable.forEach((value, key) => {
            if (value === barcode) {
                product = productsInfo.get(barcode);
                found = true;
                imageUrl = product.imageUrl;
                maxAmount++;
                barcodeProduct = value;
            }
        });
        if (!found) {
            document.getElementById("error").textContent = document.getElementById("textProductNotFound").textContent;
        } else {
            let barcode = document.getElementById("barcode").firstElementChild;
            document.getElementById("image").src = imageUrl;
            document.getElementById("error").textContent = "";
            document.getElementById("inputAmount").value = "1";
            barcode.value = "";
            checkBarcode(barcode);
            checkAmount(document.getElementById("inputAmount"));
        }
    }
}

function addProduct(e) {
    const number = parseInt(e.value);
    let count = 0;
    let barcode;
    let product;
    if (okAmount) {
        if (number > maxAmount) {
            document.getElementById("error").textContent = document.getElementById("textTooManyProducts").textContent + maxAmount;
        } else if (number !== 0) {
            document.getElementById("orderButton").className = "greenButton";
            productsAvailable.forEach((value, key) => {
                if (value === barcodeProduct && count !== number) {
                    count++;
                    productsAvailable.delete(key);
                    productsBought.set(key, value);
                    barcode = value;
                }
            });
            if (productsBarcode.has(barcode)) {
                productsBarcode.set(barcode, productsBarcode.get(barcode) + count);
            } else {
                productsBarcode.set(barcode, count);
            }
            if (productsVisible.indexOf(barcode) !== -1) {
                document.getElementById(barcode).getElementsByClassName("quantity")[0].textContent = productsBarcode.get(barcode);
            } else if (productsVisible.length < 3) {
                productsVisible.push(barcode);
                product = productsInfo.get(barcode);
                createResult(product, barcode, true);
            }
            barcodeProduct = null;
            maxAmount = 0;
            document.getElementById("image").src = "/images/nothing.png";
            document.getElementById("inputAmount").value = "";
            checkAmount(document.getElementById("inputAmount"));
        }
    }
}

function goLeft() {
    if (index !== 0) {
        index--;
        const barcode = Array.from(productsBarcode.keys())[index];
        const product = productsInfo.get(barcode);
        document.getElementById(productsVisible[2]).remove();
        productsVisible[1] = productsVisible[0];
        productsVisible[2] = productsVisible[1];
        productsVisible[0] = barcode;
        createResult(product, barcode, false);
        checkCanClickLeftButton(document.getElementById("leftButton"));
    }
}

function goRight() {
    if (index + 3 < productsBarcode.size) {
        index++;
        const barcode = Array.from(productsBarcode.keys())[index + 2];
        const product = productsInfo.get(barcode);
        document.getElementById(productsVisible[0]).remove();
        productsVisible[0] = productsVisible[1];
        productsVisible[1] = productsVisible[2];
        productsVisible[2] = barcode;
        createResult(product, barcode, true);
        checkCanClickRightButton(document.getElementById("rightButton"));
    }
}

function createResult(product, barcode, type) {
    let result = document.createElement("div");
    result.className = "result";
    result.id = barcode;
    result.innerHTML = `
                        <div class="result-vertical">
                        <div class="result-horizontal notImageContainer">
                            <span class="name">` + product.name + `</span>
                            <span class="quantity">` + productsBarcode.get(barcode) + `</span>
                            <a class="resultIcons" onclick="deleteProducts('` + barcode + `')">
                                <img src="/images/close.png" alt="close" class="imageClose">
                            </a>
                        </div>
                        <div class="result-horizontal imageContainer">
                            <img class="productImage" src="` + product.imageUrl + `" alt="product">
                            <div class="description">` + product.description + `
                            </div>
                        </div>
                        <div class="result-horizontal notImageContainer foot">
                        </div>
                    </div>
                        `
    if (type) {
        document.getElementById("results").appendChild(result);
    } else {
        document.getElementById("results").prepend(result);
    }
}

function checkCanClickLeftButton(e) {
    if (index !== 0) {
        e.style.backgroundColor = "#E3E1E1";
        e.style.cursor = "pointer";
    } else {
        hideButton(e);
    }
}

function checkCanClickRightButton(e) {
    if (index + 3 < productsBarcode.size) {
        e.style.backgroundColor = "#E3E1E1";
        e.style.cursor = "pointer";
    } else {
        hideButton(e);
    }
}

function hideButton(e) {
    e.style.backgroundColor = "#F4F2F2";
    e.style.cursor = "auto";
}

function deleteProducts(barcode) {
    productsBought.forEach((value, key) => {
        if (value === barcode) {
            productsBought.delete(key);
            productsAvailable.set(key, value);
        }
    });
    document.getElementById(barcode).remove();
    productsBarcode.delete(barcode);
    const productIndex = productsVisible.indexOf(barcode);
    if (productIndex !== -1) {
        productsVisible.splice(productIndex, 1);
        if (index + 3 <= productsBarcode.size) {
            const barcode = Array.from(productsBarcode.keys())[index + 2];
            const product = productsInfo.get(barcode);
            productsVisible[2] = barcode;
            createResult(product, barcode, true);
        }
    }
    if (productsBought.size === 0) {
        document.getElementById("orderButton").className = "grayButton";
    }
}

function buyProducts() {
    if (productsBought.size > 0) {
        fetch("/finish_order", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(Array.from(productsBought.keys()))
        })
            .then(content => content.text())
            .then(content => {
                if (content === "true") {
                    window.location = "/?success";
                } else {
                    document.getElementById("error").textContent = document.getElementById("textBadProducts").textContent;
                }
            })
            .catch(error => {
                console.error(error);
            });
    }
}