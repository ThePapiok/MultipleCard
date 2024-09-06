function atStart(page, isDescending, field) {
    checkLanguage();
    let selectedPage = document.getElementById("page" + page);
    if (selectedPage !== null) {
        selectedPage.classList.add("selectedPage");
    }
    let searchSelect = document.getElementById("searchSelect");
    if (isDescending === "true") {
        switch (field) {
            case "review.createdAt":
                searchSelect.value = 2;
                break;
            case "review.rating":
                searchSelect.value = 4;
                break;
        }
    } else {
        switch (field) {
            case "count":
                searchSelect.value = 1;
                break;
            case "review.createdAt":
                searchSelect.value = 3;
                break;
            case "review.rating":
                searchSelect.value = 5;
                break;
        }
    }
}

function setParams() {
    const value = parseInt(document.getElementById("searchSelect").value);
    const text = document.getElementById("searchInput").value;
    let sort = null;
    let url = "/reviews";
    switch (value) {
        case 1:
            sort = "isDescending=false";
            break;
        case 2:
            sort = "field=review.createdAt";
            break;
        case 3:
            sort = "field=review.createdAt&isDescending=false";
            break;
        case 4:
            sort = "field=review.rating";
            break;
        case 5:
            sort = "field=review.rating&isDescending=false";
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