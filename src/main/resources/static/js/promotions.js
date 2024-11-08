function setPromotions(promotions, type) {
    let prices = document.getElementsByClassName("price");
    let price;
    let noPromotionContainer;
    let promotionContainer;
    let promotion
    let amount;
    let count;
    let resultVertical;
    for (let i = 0; i < prices.length; i++) {
        price = prices[i];
        for (let j = 0; j < promotions.length; j++) {
            promotion = promotions[j];
            if (promotion.productId === price.dataset.id) {
                amount = promotion.amount;
                count = promotion.count;
                if (type) {
                    price.parentElement.firstElementChild.firstElementChild.nextElementSibling.nextElementSibling.firstElementChild.src = "/images/discount_fill.png";
                }
                noPromotionContainer = price.firstElementChild;
                noPromotionContainer.hidden = true;
                promotionContainer = noPromotionContainer.nextElementSibling;
                promotionContainer.hidden = false;
                resultVertical = promotionContainer.firstElementChild.firstElementChild;
                if (count !== 0) {
                    resultVertical.firstElementChild.nextElementSibling.textContent = promotion.count + ' ' + document.getElementById("textLeftProducts").textContent;
                }
                resultVertical.nextElementSibling.firstElementChild.nextElementSibling.textContent = amount / 100 + "zł";
                resultVertical.firstElementChild.firstElementChild.textContent = promotion.startAt;
                resultVertical.firstElementChild.firstElementChild.nextElementSibling.nextElementSibling.textContent = promotion.expiredAt;
                promotions.splice(j, 1);
            }
        }
    }
}