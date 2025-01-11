let blockAndDeleteVar = false;
let blockUserVar = false;
let deleteReviewVar = false;
let deleteProductVar = false;
let rejectReportVar = false;
let isBlockAndDeleteVar = false;
let isBlockUserVar = false;
let isDeleteReviewVar = false;
let isDeleteProductVar = false;
let isRejectReportVar = false;

function atStart() {
    checkLanguage();
}

function showOrHideBlockAndDelete(id, e, isProduct) {
    blockAndDeleteVar = !blockAndDeleteVar;
    let confirmation = document.getElementById("blockAndDelete");
    confirmation.hidden = !blockAndDeleteVar;
    if (blockAndDeleteVar) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
        confirmation.dataset.isProduct = isProduct;
    } else {
        confirmation.dataset.id = "";
        confirmation.dataset.isProduct = "";
        isBlockAndDeleteVar = false;

    }
}

function showOrHideBlockUserReported(id, e, isProduct) {
    blockUserVar = !blockUserVar;
    let confirmation = document.getElementById("blockReported");
    confirmation.hidden = !blockUserVar;
    if (blockUserVar) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
        confirmation.dataset.isProduct = isProduct;
    } else {
        confirmation.dataset.id = "";
        isBlockUserVar = false;
    }
}

function showOrHideDeleteReviewReport(id, e) {
    deleteReviewVar = !deleteReviewVar;
    let confirmation = document.getElementById("deleteReview");
    confirmation.hidden = !deleteReviewVar;
    if (deleteReviewVar) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isDeleteReviewVar = false;
    }
}

function showOrHideDeleteProductReport(id, e) {
    deleteProductVar = !deleteProductVar;
    let confirmation = document.getElementById("deleteProduct");
    confirmation.hidden = !deleteProductVar;
    if (deleteProductVar) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isDeleteProductVar = false;
    }
}

function showOrHideRejectReport(id, e) {
    rejectReportVar = !rejectReportVar;
    let confirmation = document.getElementById("reject");
    confirmation.hidden = !rejectReportVar;
    if (rejectReportVar) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isRejectReportVar = false;
    }
}

function rejectReport() {
    if (!isRejectReportVar) {
        isRejectReportVar = true;
        let button = document.getElementById("buttonConfirmationReject");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("reject");
        fetch("/reject_report", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideRejectReport();
                if (content === "true") {
                    location.reload();
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
            .catch(error => {
                console.error(error);
            })
    }
}

function blockUserReported() {
    if (!isBlockUserVar) {
        isBlockUserVar = true;
        let button = document.getElementById("buttonConfirmationBlockReported");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("blockReported");
        fetch("/block_at_report", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
                "isProduct": confirmation.dataset.isProduct
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideBlockUserReported();
                if (content === "true") {
                    location.reload();
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
    }
}

function deleteProductReport() {
    if (!isDeleteProductVar) {
        isDeleteProductVar = true;
        let button = document.getElementById("buttonConfirmationDeleteProduct");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("deleteProduct");
        fetch("/delete_product", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideDeleteProductReport();
                if (content === "true") {
                    location.reload();
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
    }
}

function deleteReviewReport() {
    if (!isDeleteReviewVar) {
        isDeleteReviewVar = true;
        let button = document.getElementById("buttonConfirmationDeleteReview");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("deleteReview");
        fetch("/delete_review", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideDeleteReviewReport();
                if (content === "true") {
                    location.reload();
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
    }
}

function blockAndDelete() {
    if (!isBlockAndDeleteVar) {
        isBlockAndDeleteVar = true;
        let button = document.getElementById("buttonConfirmationBlockAndDelete");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("blockAndDelete");
        fetch("/delete_and_block", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
                "isProduct": confirmation.dataset.isProduct
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideBlockAndDelete();
                if (content === "true") {
                    location.reload();
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
    }
}

