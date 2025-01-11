let deleteProd = false;
let isDeleted = false;

function showOrHideDeleteProduct(id, e) {
    deleteProd = !deleteProd;
    let confirmation = document.getElementById("delete");
    confirmation.hidden = !deleteProd;
    if (deleteProd) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isDeleted = false;
    }
}

function deleteProductAdmin() {
    if (!isDeleted) {
        isDeleted = true;
        let button = document.getElementById("buttonConfirmationDelete");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("delete");
        fetch("/delete_product", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideDeleteProduct();
                if (content === "true") {
                    location.reload();
                    document.getElementById("success").textContent = document.getElementById("textSuccessDelete").textContent;
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
    }
}
