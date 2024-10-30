function checkOnlyIfOther(input, cond, i, startInput, hasCheck, e, type) {
    if (input === startInput) {
        deleteCheck(i, hasCheck, e, type);
    } else {
        e.style.color = "black";
        previous[i - 1] = !cond;
        check(i, cond, ok, previous, buttonId, success, hasCheck, true);
    }
}

function deleteCheck(i, hasCheck, e, type) {
    if (hasCheck) {
        document.getElementById("close" + i).hidden = true;
        document.getElementById("check" + i).hidden = true;
    }
    if (e != null) {
        e.style.color = "lightgray";
        ok[i - 1] = null;
        previous[i - 1] = null;
    }
    let cond = true;
    let isFound;
    if (type != null && ok[5] != null) {
        isFound = false;
        for (let j = 6; j < ok.length; j++) {
            if (ok[j] != null) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            ok[5] = null;
            previous[5] = null;
        }
    }
    activeButton(buttonId, ok, success, true);
    disableButton(buttonId, ok, success, true);
    for (let j = 0; j <= ok.length; j++) {
        if (ok[j] != null && ok[j] === false) {
            cond = false;
        }
    }
}
