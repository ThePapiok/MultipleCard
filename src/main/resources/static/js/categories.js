let category = false;
let indexCategory = 1;
let okTypedCategory = false;
const regCategory = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ]([a-ząćęłńóśźż]*|[a-ząćęłńóśźż]* [a-ząćęłńóśźż]+)$")

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

function setValueCategory(e, typed, isEdit) {
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
        if (!isEdit) {
            check(2, true, ok, previous, buttonId, success, true, true);
        }
        document.getElementById("category" + indexCategory).value = category;
        document.getElementById("inputCategory" + indexCategory).value = category;
        document.getElementById("delete" + indexCategory).style.display = "block";
        document.getElementById("categoryInput").value = "";
        document.getElementById("typedCategory").textContent = "";
        document.getElementById("closeTypedCategory").hidden = false;
        document.getElementById("checkTypedCategory").hidden = true;
        okTypedCategory = false;
        indexCategory++;
        if (isEdit) {
            checkCategories();
        }
    }
    showOrHideCategory();
}

function deleteCategory(e, isEdit) {
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
    if (isEdit) {
        if (indexCategory === 1) {
            previous[3] = true;
            check(4, false, ok, previous, buttonId, success, true, true);
        } else {
            checkCategories();
        }
    } else {
        if (indexCategory === 1) {
            check(2, false, ok, previous, buttonId, success, true, true);
        }
    }
}

function checkCategories() {
    let findOther = false;
    const selectedCategories = document.getElementsByClassName("selectedCategory");
    for (let i = 0; i < selectedCategories.length; i++) {
        if (selectedCategories[i].firstElementChild.value === "") {
            break;
        }
        if (!categories.includes(selectedCategories[i].firstElementChild.value)) {
            findOther = true;
            break;
        }
    }
    if (findOther) {
        previous[3] = false;
        check(4, true, ok, previous, buttonId, success, true, true);
    } else {
        check(4, false, ok, previous, buttonId, success, false, true);
        document.getElementById("close4").hidden = true;
        document.getElementById("check4").hidden = true;
        ok[3] = null;
        previous[3] = null;
        for (let i = 0; i < ok.length; i++) {
            if (ok[i]) {
                previous[i] = false;
                check(i + 1, true, ok, previous, buttonId, success, false, true);
                break;
            }
        }
    }
}
