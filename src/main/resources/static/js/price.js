const regPrice = new RegExp("^[0-9]*\\.?[0-9]{2}(zł)?$")

function focusedPrice(e) {
    if (e.value.toString().includes("zł")) {
        e.value = e.value.toString().replaceAll("zł", "");
    }
}

function unfocusedPrice(e) {
    if (!e.value.toString().includes(".")) {
        e.value += ".00";
    }
    if (!e.value.toString().includes("zł")) {
        e.value += "zł";
    }
    checkPrice(e);
}