let deleteCat = false;
let isDeleted = false;

function setPageWithName() {
    window.location = "/categories?name=" + document.getElementById("searchInput").value;
}

function atStart(maxPage, page) {
    checkLanguage();
    let selectedPage = document.getElementById("page" + page);
    if (selectedPage != null) {
        selectedPage.classList.add("selectedPage");
    }
    checkButtonPages(page, maxPage);
}

function showOrHideDeleteCategory(name, e) {
    deleteCat = !deleteCat;
    let confirmation = document.getElementById("delete");
    confirmation.hidden = !deleteCat;
    if (deleteCat) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.name = name;
    } else {
        confirmation.dataset.name = "";
        isDeleted = false;
    }
}

function deleteCategory() {
    if (!isDeleted) {
        isDeleted = true;
        let button = document.getElementById("buttonConfirmationDelete");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("delete");
        fetch("/delete_category", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "name": confirmation.dataset.name,
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideDeleteCategory();
                if (content === "true") {
                    location.reload();
                    document.getElementById("success").textContent = document.getElementById("textSuccessDelete").textContent;
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })
    }
}
