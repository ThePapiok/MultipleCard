let ok = [false, false, false, false, false, false];
let previous = [false, false, false, false, false, false];
let category = false;
let indexCategory = 1;
let okTypedCategory = false;
const buttonId = "addButton";
const regProductName = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż][a-ząćęłńóśźż ]*$")
const regCategory = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ]([a-ząćęłńóśźż]*|[a-ząćęłńóśźż]* [a-ząćęłńóśźż]+)$")
const regBarcode = new RegExp("^[0-9]*$")

function checkAmount(e, index) {
    const input = e.value;
    const length = input.length;
    check(index, (length >= 2 && length <= 7 && regAmount.test(input)), ok, previous, buttonId, success, true, true);
}

function checkDescription(e, index) {
    const length = e.value.length;
    check(index, (length >= 5 && length <= 100), ok, previous, buttonId, success, true, true);
}

function checkProductName(e, index) {
    const input = e.value;
    const length = input.length;
    check(index, (length >= 2 && length <= 30 && regProductName.test(input)), ok, previous, buttonId, success, true, true);
}

function checkCategory(e) {
    const input = e.value;
    const length = input.length;
    const text = input.toString().toLowerCase();
    let options = document.getElementsByClassName("optionSelect");
    document.getElementById("typedCategory").textContent = input;
    if (length >= 2 && length <= 15 && regCategory.test(input)) {
        document.getElementById("checkTypedCategory").hidden = false;
        document.getElementById("closeTypedCategory").hidden = true;
        okTypedCategory = true;
    } else {
        document.getElementById("checkTypedCategory").hidden = true;
        document.getElementById("closeTypedCategory").hidden = false;
        okTypedCategory = false;
    }
    let option;
    for (let i = 0; i < options.length; i++) {
        option = options[i];
        if (option.textContent.toLowerCase().startsWith(text)) {
            option.style.display = "block";
        } else {
            option.style.display = "none";
        }
    }
}

function checkBarcode(e, index) {
    const input = e.value;
    const length = input.length;
    check(index, (length === 13 && regBarcode.test(input)), ok, previous, buttonId, success, true, true);
}

function atStart() {
    checkLanguage();
}

function showOrHideCategory() {
    category = !category;
    if (category) {
        document.getElementById("categories").style.display = "block";
        document.getElementById("optionsCategory").style.display = "block";
    } else {
        document.getElementById("categories").style.display = "none";
        document.getElementById("optionsCategory").style.display = "none";
    }
}

function setValueCategory(e, index, typed) {
    let find = false;
    const category = e.textContent;
    const selectedCategory = document.getElementsByClassName("selectedCategory");
    for (let i = 0; i < selectedCategory.length; i++) {
        if (selectedCategory[i].firstElementChild.value === category) {
            find = true;
            break;
        }
    }
    if (indexCategory <= 3 && !find && (!typed || (typed && okTypedCategory))) {
        check(2, true, ok, previous, buttonId, success, true, true);
        document.getElementById("category" + indexCategory).value = category;
        document.getElementById("inputCategory" + indexCategory).value = category;
        document.getElementById("delete" + indexCategory).style.display = "block";
        document.getElementById("categoryInput").value = "";
        document.getElementById("typedCategory").textContent = "";
        document.getElementById("closeTypedCategory").hidden = false;
        document.getElementById("checkTypedCategory").hidden = true;
        okTypedCategory = false;
        indexCategory++;
    }
    showOrHideCategory();
}

function deleteCategory(e) {
    indexCategory--;
    let selected = e.parentElement;
    let sibling = selected.nextElementSibling;
    let firstElementChildSelected;
    let firstElementChildSibling;
    const id = e.id;
    const index = parseInt(id.charAt(id.length - 1));
    for (let i = index; i < indexCategory; i++) {
        firstElementChildSelected = selected.firstElementChild;
        firstElementChildSibling = sibling.firstElementChild;
        firstElementChildSelected.value = firstElementChildSibling.value;
        firstElementChildSelected.nextElementSibling.value = firstElementChildSibling.nextElementSibling.value;
        selected = sibling;
        sibling = sibling.nextElementSibling;
    }
    firstElementChildSelected = selected.firstElementChild;
    firstElementChildSelected.value = "";
    firstElementChildSelected.nextElementSibling.value = document.getElementById("textCategoryNotSelected").textContent;
    document.getElementById("delete" + indexCategory).style.display = "none";
    if (indexCategory === 1) {
        check(2, false, ok, previous, buttonId, success, true, true);
    }
}
