let deleteProd = false;
let blockProd = false;

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

function productInfo(productId) {
    window.location.href = "/products?id=" + productId;
}