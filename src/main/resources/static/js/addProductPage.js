let ok = [false, false, false, false, false, false];
let previous = [false, false, false, false, false, false];
const buttonId = "addButton";

function checkPrice(e) {
    e = replaceComma(e);
    e = checkIsMaxPrice(e);
    const input = e.value;
    const length = input.length;
    check(3, (length >= 5 && length <= 9 && regPrice.test(input)), ok, previous, buttonId, success, true, true);
}

function checkDescription(e, index) {
    const length = e.value.length;
    check(index, (length >= 5 && length <= 1000), ok, previous, buttonId, success, true, true);
}

function checkProductName(e, index) {
    const input = e.value;
    const length = input.length;
    check(index, (length >= 2 && length <= 60 && regProductName.test(input)), ok, previous, buttonId, success, true, true);
}

function checkBarcode(e, index) {
    const input = e.value;
    const length = input.length;
    check(index, (length === 13 && regBarcode.test(input)), ok, previous, buttonId, success, true, true);
}

function atStart() {
    checkProductName(document.getElementById("name"), 1);
    unfocusedPrice(document.getElementById("price"));
    checkPrice(document.getElementById("price"));
    checkBarcode(document.getElementById("barcode"), 4);
    checkDescription(document.getElementById("description"), 5);
    setCategories();
    check(2, true, ok, previous, buttonId, success, true, true);
    checkLanguage();
}