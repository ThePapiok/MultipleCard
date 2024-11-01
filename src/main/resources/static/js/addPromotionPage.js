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
    const cond = (input !== "" && input >= today);
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
    const cond = (input !== "" && input >= document.getElementById("startAt").value && input >= today);
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
    const cond = (length >= 2 && length <= 7 && regAmount.test(input) && parseFloat(input.toString().replace("zł", "")) < originalAmount);
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
    today = today.getFullYear().toString() + "-" + (today.getMonth() + 1).toString() + "-" + today.getDate().toString();
    checkLanguage();
    document.getElementById("startAt").value = document.getElementById("dateStartAt").textContent;
    document.getElementById("expiredAt").value = document.getElementById("dateExpiredAt").textContent;
    if (document.getElementById("hasPromotion").textContent === "true") {
        ok.push(null, null, null, null);
        previous.push(null, null, null, null);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonEditPromotion").textContent;
        edit = true;
        startAt = document.getElementById("startAt").value;
        expiredAt = document.getElementById("expiredAt").value;
        count = document.getElementById("count").value;
        originalAmount = parseFloat(document.getElementById("originalAmount").textContent);
        unfocusedAmount(document.getElementById("amount"));
        document.getElementById("amount").value = document.getElementById("amount").value + " (" + originalAmount + "zł)";
        amount = document.getElementById("amount").value;

    } else {
        ok.push(false, false, false, true);
        previous.push(false, false, false, true);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonAddPromotion").textContent;
        edit = false;
        document.getElementById("amount").value = "";
    }
    checkStartAt(document.getElementById("startAt"), false);
    checkCount(document.getElementById("count"));
    checkAmount(document.getElementById("amount"))
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
                document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
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
        e.value = (originalAmount * parseInt(e.value.substring(0, index)) / 100.0).toFixed(2);
        checkAmount(e);
        unfocusedAmount(e);
    }
    e.value = e.value + " (" + originalAmount + "zł)";
}
