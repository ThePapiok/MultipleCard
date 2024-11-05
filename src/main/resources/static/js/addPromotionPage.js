let ok = [];
let previous = [];
let buttonId = "promotionButton";
let edit;
let startAt;
let expiredAt;
let amount;
let count;
let originalAmount;
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

function checkAmount(e) {
    const input = e.value;
    const length = input.length;
    let cond = (length >= 2 && length <= 7 && regAmount.test(input) && parseFloat(input.toString().replace("zł", "")) < originalAmount);
    if (edit) {
        checkOnlyIfOther(input, cond, 3, amount, true, e);
    } else {
        check(3, cond, ok, previous, buttonId, success, true, true);
    }
}

function checkCount(e) {
    let input = e.value;
    if (input === "0") {
        input = "";
        e.value = "";
    }
    const cond = (input === "" || (input.length <= 5 && regNumber.test(input)));
    if (edit) {
        checkOnlyIfOther(input, cond, 4, count, true, e);
    } else {
        check(4, cond, ok, previous, buttonId, success, true, true);
    }
}

function atStart() {
    let suffix;
    today.setHours(0, 0, 0, 0);
    originalAmount = parseFloat(document.getElementById("originalAmount").textContent);
    suffix = originalAmount.toString().substring(originalAmount.length - 3, originalAmount.length);
    if (suffix.charAt(1) === ".") {
        originalAmount += "0";
    } else if (suffix.charAt(2) === ".") {
        originalAmount += "00";
    } else if (!suffix.includes(".")) {
        originalAmount += ".00";
    }
    checkLanguage();
    document.getElementById("startAt").value = document.getElementById("dateStartAt").textContent;
    document.getElementById("expiredAt").value = document.getElementById("dateExpiredAt").textContent;
    if (document.getElementById("hasPromotion").textContent === "true") {
        let amountInput = document.getElementById("amount");
        ok.push(null, null, null, null);
        previous.push(null, null, null, null);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonEditPromotion").textContent;
        edit = true;
        startAt = document.getElementById("startAt").value;
        expiredAt = document.getElementById("expiredAt").value;
        count = document.getElementById("count").value;
        unfocusedAmount(amountInput);
        amount = amountInput.value;
        checkAmount(amountInput);
        amountInput.value = amountInput.value + " (" + originalAmount + "zł)";
    } else {
        ok.push(false, false, false, true);
        previous.push(false, false, false, true);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonAddPromotion").textContent;
        edit = false;
        document.getElementById("amount").value = "";
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
    focusedAmount(e);
    e.value = e.value.toString().substring(0, e.value.toString().indexOf("(") - 1);
}

function unfocused(e) {
    const index = e.value.indexOf("%");
    if (index === -1) {
        unfocusedAmount(e);
    } else if (index === (e.value.length - 1) && index === e.value.lastIndexOf("%")) {
        e.value = parseFloat(originalAmount) * parseInt(e.value.substring(0, index)) / 100.0.toFixed(2);
        checkAmount(e);
        unfocusedAmount(e);
    }
    e.value = e.value + " (" + originalAmount + "zł)";
}
