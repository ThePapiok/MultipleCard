let ok = [false, false];
let previous = [false, false];
const buttonId = "searchCardButton";
const regCardId = new RegExp("^[0-9a-f]*$");


function atStart() {
    checkLanguage();
}

function checkCardId(e) {
    const value = e.value;
    const length = value.length;
    check(1, (length === 24 && regCardId.test(value)), ok, previous, buttonId, success, true, true);
}

function checkCardName(e) {
    const value = e.value;
    const length = value.length;
    check(2, (length >= 4 && length <= 9 && regCardName.test(value)), ok, previous, buttonId, success, true, true);
}

document.addEventListener("keydown", (event) => {
    let cardId = document.getElementById("cardId");
    let link;
    if (event.key === "Enter" && document.activeElement === cardId) {
        link = cardId.value.toString();
        if (link.indexOf("?id=") !== -1) {
            cardId.value = link.substring(link.indexOf("?id=") + 4);
            checkCardId(cardId);
        }
    }
});