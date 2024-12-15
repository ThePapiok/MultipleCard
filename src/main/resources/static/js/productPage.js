let ok = [null, null, null, null, null, null];
let previous = [null, null, null, null, null, null];
let price;
let name;
let barcode;
let description;
const buttonId = "saveButton";

function atStart() {
    let realPrice;
    let suffix;
    setCategories();
    name = document.getElementById("name").value;
    realPrice = (parseInt(document.getElementById("price").value) / 100.0).toString();
    suffix = realPrice.substring(realPrice.length - 3, realPrice.length);
    if (suffix.charAt(1) === ".") {
        realPrice += "0";
    } else if (suffix.charAt(2) === ".") {
        realPrice += "00";
    } else if (!suffix.includes(".")) {
        realPrice += ".00";
    }
    document.getElementById("price").value = realPrice;
    unfocusedPrice(document.getElementById("price"));
    price = document.getElementById("price").value;
    checkPrice(document.getElementById("price"));
    barcode = document.getElementById("barcode").value;
    description = document.getElementById("inputEdit").value;
}

function checkName(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length >= 2 && length <= 60 && regProductName.test(input)), 1, name, true, e);
}

function checkPrice(e) {
    e = replaceComma(e);
    e = checkIsMaxPrice(e);
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length >= 5 && length <= 9 && regPrice.test(input)), 3, price, true, e);
}

function checkBarcode(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length === 13 && regBarcode.test(input)), 2, barcode, true, e);
}

function checkDescription(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length >= 5 && length <= 1000), 5, description, true, e);
}