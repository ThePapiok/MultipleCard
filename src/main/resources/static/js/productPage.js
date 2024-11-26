let ok = [null, null, null, null, null, null];
let previous = [null, null, null, null, null, null];
let price;
let name;
let barcode;
let description;
const buttonId = "saveButton";

function atStart() {
    let realIndex;
    let category;
    const length = categories.length;
    indexCategory = length + 1;
    for (let i = 0; i < length; i++) {
        category = categories[i];
        realIndex = i + 1;
        document.getElementById("category" + realIndex).value = category;
        document.getElementById("inputCategory" + realIndex).value = category;
        document.getElementById("delete" + realIndex).style.display = "block";
    }
    document.getElementById("price").value /= 100.0;
    name = document.getElementById("name").value;
    unfocusedPrice(document.getElementById("price"));
    price = document.getElementById("price").value;
    checkPrice(document.getElementById("price"));
    barcode = document.getElementById("barcode").value;
    description = document.getElementById("inputEdit").value;
}

function checkName(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length >= 2 && length <= 30 && regProductName.test(input)), 1, name, true, e);
}

function checkPrice(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length >= 2 && length <= 7 && regPrice.test(input)), 3, price, true, e);
}

function checkBarcode(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length === 13 && regBarcode.test(input)), 2, barcode, true, e);
}

function checkDescription(e) {
    const input = e.value;
    const length = input.length;
    checkOnlyIfOther(input, (length >= 5 && length <= 100), 5, description, true, e);
}