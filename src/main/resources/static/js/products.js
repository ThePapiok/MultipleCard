function setParams(url) {
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
        case 6:
            sort = "field=added";
            break;
        case 7:
            sort = "field=added&isDescending=false";
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
    window.location = url;
}

function atStart(page, isDescending, field, maxPage, type) {
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
            case "added":
                searchSelect.value = 6;
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
            case "added":
                searchSelect.value = 7;
                break;
        }
    }
    checkButtonPages(page, maxPage);
}