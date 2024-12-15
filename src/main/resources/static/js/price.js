const regPrice = new RegExp("^[0-9]*\\.?[0-9]{2}(zł)?$")

function focusedPrice(e) {
    const value = e.value.toString();
    if (value.includes("zł")) {
        e.value = value.replaceAll("zł", "");
    }
}

function unfocusedPrice(e) {
    const value = e.value.toString();
    const length = value.length;
    if (e.value !== "" && e.value !== "0") {
        if (value.charAt(length - 1) === "."){
            e.value += "00";
        }
        else if (value.charAt(length - 2) === "."){
            e.value += "0";
        }
        else if (!value.includes(".")) {
            e.value += ".00";
        }
        if (!value.includes("zł")) {
            e.value += "zł";
        }
        checkPrice(e);
    }
}

function replaceComma(e) {
    let price = e.value.toString();
    const lastCharIndex = price.length - 1;
    if (price.charAt(lastCharIndex) === ","){
        price = price.substring(0, lastCharIndex) + ".";
        e.value = price;
    }
    return e;
}

function checkIsMaxPrice(e){
    let price = e.value.toString();
    const lastCharIndex = price.length - 1;
    if (lastCharIndex === 4 && price.indexOf(".") === -1){
        price = price.substring(0, lastCharIndex);
        e.value = price;
    }
    return e;
}
