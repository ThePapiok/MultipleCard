function clear() {
    document.getElementById("check1").checked = false;
    document.getElementById("check2").checked = false;
    document.getElementById("check3").checked = false;
    document.getElementById("check4").checked = false;
    document.getElementById("check5").checked = false;
}

function checkStar(e) {
    const id = e.id.toString();
    const index = parseInt(id.charAt(id.length - 1));
    const next = "check" + (index + 1);
    if (e.checked === true || document.getElementById(next).checked === true) {
        clear();
        e.checked = true;
        for (let i = 1; i < index; i++) {
            document.getElementById("check" + i).checked = true;
        }
    } else {
        clear();
    }
}

