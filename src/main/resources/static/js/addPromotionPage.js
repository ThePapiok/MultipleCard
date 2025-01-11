let ok = [];
let previous = [];
let buttonId = "promotionButton";
let edit;
let startAt;
let expiredAt;
let price;
let quantity;
let originalPrice;
let deleteProm = false;
let today = new Date();

function checkStartAt(e, oneTime) {
    const input = e.value;
    let cond;
    let startAt = new Date(input);
    startAt.setHours(0, 0, 0, 0);
    cond = (input !== "" && startAt >= today);
    if (edit) {
        checkOnlyIfOther(input, cond, 1, startAt, false, e);
    } else {
        check(1, cond, ok, previous, buttonId, success, false, true);
    }
    if (!oneTime) {
        checkExpiredAt(document.getElementById("expiredAt"), true);
    }
}

function checkExpiredAt(e, oneTime) {
    const input = e.value;
    let cond;
    let expiredAt = new Date(input);
    expiredAt.setHours(0, 0, 0, 0);
    let startAt = new Date(document.getElementById("startAt").value);
    startAt.setHours(0, 0, 0, 0);
    cond = (input !== "" && expiredAt >= today && expiredAt >= startAt);
    if (edit) {
        checkOnlyIfOther(input, cond, 2, expiredAt, false, e);
    } else {
        check(2, cond, ok, previous, buttonId, success, false, true);
    }
    if (!oneTime) {
        checkStartAt(document.getElementById("startAt"), true);
    }
}

function checkPrice(e) {
    e = replaceComma(e);
    e = checkIsMaxPrice(e);
    let valuePercentage;
    let realPrice = e.value.toString();
    const lastCharIndex = realPrice.length - 1;
    if (realPrice.charAt(lastCharIndex) === "%") {
        valuePercentage = parseFloat(realPrice.substring(0, lastCharIndex));
        if (valuePercentage != null) {
            e.value = (parseFloat(originalPrice) * parseInt(e.value.substring(0, lastCharIndex)) / 100.0).toFixed(2);
            checkPrice(e);
            unfocusedPrice(e);
        }

    }
    const input = e.value;
    const length = input.length;
    let cond = (length >= 5 && length <= 9 && regPrice.test(input) && parseFloat(input.toString().replace("zł", "")) < originalPrice);
    if (edit) {
        checkOnlyIfOther(input, cond, 3, price, true, e);
    } else {
        check(3, cond, ok, previous, buttonId, success, true, true);
    }
}

function checkQuantity(e) {
    let input = e.value;
    if (input === "0") {
        input = "";
        e.value = "";
    }
    const cond = (input === "" || (input.length <= 5 && regNumber.test(input)));
    if (edit) {
        checkOnlyIfOther(input, cond, 4, quantity, true, e);
    } else {
        check(4, cond, ok, previous, buttonId, success, true, true);
    }
}

function atStart() {
    today.setHours(0, 0, 0, 0);
    originalPrice = parseFloat(document.getElementById("originalPrice").textContent);
    checkLanguage();
    document.getElementById("startAt").value = document.getElementById("dateStartAt").textContent;
    document.getElementById("expiredAt").value = document.getElementById("dateExpiredAt").textContent;
    if (document.getElementById("hasPromotion").textContent === "true") {
        let priceInput = document.getElementById("newPrice");
        ok.push(null, null, null, null);
        previous.push(null, null, null, null);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonEditPromotion").textContent;
        edit = true;
        startAt = document.getElementById("startAt").value;
        expiredAt = document.getElementById("expiredAt").value;
        quantity = document.getElementById("quantity").value;
        unfocusedPrice(priceInput);
        price = priceInput.value;
        checkPrice(priceInput);
        checkQuantity(document.getElementById("quantity"));
        priceInput.value = priceInput.value + " (" + originalPrice + "zł)";
    } else {
        ok.push(false, false, false, true);
        previous.push(false, false, false, true);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonAddPromotion").textContent;
        edit = false;
        document.getElementById("newPrice").value = "";
    }

}

function showOrHideDeletePromotion() {
    deleteProm = !deleteProm;
    document.getElementById("delete").hidden = !deleteProm;
}

function deletePromotion(id) {
    fetch("/promotions", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id,
        })
    }).then(response => response.text())
        .then(response => {
            if (response === "ok") {
                window.location.href = "/products";
            } else {
                showOrHideDeletePromotion();
                document.getElementById("error").textContent = response;
            }
        }).catch((error) => {
        console.error(error);
    });
}

function focused(e) {
    focusedPrice(e);
    e.value = e.value.toString().substring(0, e.value.toString().indexOf("(") - 1);
}

function unfocused(e) {
    if (e.value !== "" && e.value !== "0") {
        unfocusedPrice(e);
        e.value = e.value + " (" + originalPrice + "zł)";
    }
}
