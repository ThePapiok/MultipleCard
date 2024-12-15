function setParams(url, type) {
    const value = parseInt(document.getElementById("searchSelect").value);
    const text = document.getElementById("searchInput").value;
    let sort = null;
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
        case 4:
            sort = "field=price";
            break;
        case 5:
            sort = "field=price&isDescending=false";
            break;
    }
    if (sort != null) {
        if (url.indexOf("?") === -1) {
            url += ("?" + sort);
        } else {
            url += ("&" + sort);
        }

    }
    if (text !== "") {
        if (url.indexOf("?") === -1) {
            url += ("?text=" + text);
        } else {
            url += ("&text=" + text);
        }
    }
    if (type) {
        const category = document.getElementById("categoryInput").value;
        const shopName = document.getElementById("shopNameInput").value;
        if (category !== "") {
            if (url.indexOf("?") === -1) {
                url += ("?category=" + category);
            } else {
                url += ("&category=" + category);
            }
        }
        if (shopName !== "") {
            if (url.indexOf("?") === -1) {
                url += ("?shopName=" + shopName);
            } else {
                url += ("&shopName=" + shopName);
            }
        }
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
            case "price":
                searchSelect.value = 4;
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
            case "price":
                searchSelect.value = 5;
                break;
        }
    }
    checkButtonPages(page, maxPage);
}