let ok = [];
let previous = [];
let buttonId = "promotionButton";
let edit;
let startAt;
let expiredAt;
let amount;
let count;
let deleteProm = false;
let today = new Date();

function checkStartAt(e, oneTime){
    const input = e.value;
    const cond = (input !== "" && input >= today);
    if(edit) {
        checkOnlyIfOther(input, cond, 1, startAt, false, e);
    }
    else{
        check(1, cond, ok, previous, buttonId, success, false, true);
    }
    if(!oneTime)
    {
        checkExpiredAt(document.getElementById("expiredAt"), true);
    }
}

function checkExpiredAt(e, oneTime){
    const input = e.value;
    const cond = (input !== "" && input >= document.getElementById("startAt").value && input >= today);
    if(edit) {
        checkOnlyIfOther(input, cond, 2, expiredAt, false, e);
    }
    else{
        check(2, cond, ok, previous, buttonId, success, false, true);
    }
    if(!oneTime){
        checkStartAt(document.getElementById("startAt"), true);
    }
}

function checkAmount(e){
    const input = e.value;
    const length = input.length;
    const cond = (length >= 2 && length <= 7 && regAmount.test(input));
    if(edit) {
        checkOnlyIfOther(input, cond, 3, amount, true, e);
    }
    else{
        check(3, cond, ok, previous, buttonId, success, true, true);
    }
}


function checkCount(e){
    let input = e.value;
    if (input === "0"){
        input = "";
        e.value = "";
    }
    const cond = (input === "" || (input.length <= 5 && regNumber.test(input)));
    if(edit) {
        checkOnlyIfOther(input, cond, 4, count, true, e);
    }
    else{
        check(4, cond, ok, previous, buttonId, success, true, true);
    }
}




function atStart(){
    today = today.getFullYear().toString() + "-" + (today.getMonth() + 1).toString() + "-" + today.getDate().toString();
    checkLanguage();
    document.getElementById("startAt").value = document.getElementById("dateStartAt").textContent;
    document.getElementById("expiredAt").value = document.getElementById("dateExpiredAt").textContent;
    if(document.getElementById("count").value === "0"){
        document.getElementById("count").value = "";
    }
    if (document.getElementById("hasPromotion").textContent === "true"){
        ok.push(null, null, null, null);
        previous.push(null, null, null, null);
        document.getElementById("promotionButton").textContent = document.getElementById("textButtonEditPromotion").textContent;
        edit = true;
        startAt = document.getElementById("startAt").value;
        expiredAt = document.getElementById("expiredAt").value;
        count = document.getElementById("count").value;
        amount = document.getElementById("amount").value;
    }
    else {
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

function showOrHideDeletePromotion(){
    deleteProm = !deleteProm;
    document.getElementById("deletePromotion").hidden = !deleteProm;
}

function deletePromotion(id){
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