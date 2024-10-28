function setParams() {
    const value = parseInt(document.getElementById("searchSelect").value);
    const text = document.getElementById("searchInput").value;
    let sort = null;
    let url = "/products";
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
        }
    } else {
        switch (field) {
            case "count":
                searchSelect.value = 1;
                break;
            case "date":
                searchSelect.value = 3;
                break;
        }
    }
    let prices = document.getElementsByClassName("price");
    let price;
    let noPromotionContainer;
    let promotionContainer;
    let promotion
    let amount;
    let date;
    if (promotions != null) {
        for (let i = 0; i < prices.length; i++) {
            price = prices[i];
            for (let j = 0; j < promotions.length; j++) {
                promotion = promotions[j];
                if (promotion.productId === price.dataset.id) {
                    amount = promotion.amount;
                    noPromotionContainer = price.firstElementChild;
                    noPromotionContainer.hidden = true;
                    promotionContainer = noPromotionContainer.nextElementSibling;
                    promotionContainer.hidden = false;
                    date = promotionContainer.firstElementChild.firstElementChild;
                    date.nextElementSibling.firstElementChild.nextElementSibling.textContent = amount / 100 + "zł";
                    date.firstElementChild.textContent = promotion.startAt;
                    date.firstElementChild.nextElementSibling.nextElementSibling.textContent = promotion.expiredAt;
                    promotions.splice(j, 1);
                }
            }
        }
    }
    checkButtonPages(page, maxPage);

}