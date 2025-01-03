let showOptions = false;
let showOptionsRole = false;
let searchOption = -1;

function atStart() {
    checkLanguage();
}

function showOrHideOptions(e, type, id, firstValue, secondValue) {
    const coords = e.getBoundingClientRect();
    const options = document.getElementById("options");
    options.style.width = e.offsetWidth.toString() + "px";
    let hidden = options.hidden;
    if (hidden && !showOptions && (type === "role" && (e.textContent === "ROLE_ADMIN" || e.textContent === "ROLE_USER")) || (type !== "role")) {
        showOptions = true;
        let option = document.getElementsByClassName("option");
        option[0].textContent = firstValue;
        option[1].textContent = secondValue;
        options.hidden = !hidden;
        options.style.left = coords.left + window.scrollX + 'px';
        options.style.top = coords.top + 50 + window.scrollY + 'px';
        options.dataset.type = type;
        options.dataset.id = id;
    } else if (showOptions && !hidden) {
        showOptions = false;
        options.hidden = !hidden;
    }
}

function changeUser(options, e) {
    const id = options.dataset.id;
    const type = options.dataset.type;
    const value = e.textContent;
    let option = document.getElementsByClassName("option");
    option[0].style.cursor = "wait";
    option[1].style.cursor = "wait";
    fetch("/change_user", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id,
            "type": type,
            "value": value
        })
    })
        .then(content => content.text())
        .then(content => {
            option[0].style.cursor = "pointer";
            option[1].style.cursor = "pointer";
            options.hidden = true;
            showOptions = false;
            if (content === "ok") {
                document.getElementById(id).getElementsByClassName(type)[0].textContent = value;
            } else {
                document.getElementById("error").textContent = content;
            }
        })
        .catch(error => {
            console.error(error);
        });
}

function setSearch(index, e) {
    if (searchOption !== -1) {
        document.getElementById("cell" + searchOption).style.backgroundColor = "#dcdee0";
    }
    searchOption = index;
    e.style.backgroundColor = "#C1C3C4";
}

function searchUsers(e) {
    const value = e.value;
    if (searchOption !== -1 && value.length > 0) {
        window.location = "/admin_panel?type=" + searchOption + "&value=" + value;
    }
}

function changeButton(button, e) {
    if (searchOption !== -1 && e.value.length > 0) {
        button.style.backgroundColor = "#76c75b";
    } else {
        button.style.backgroundColor = "#CFCBCB";
    }
}

function changeToGreenButton(e, input) {
    if (searchOption !== -1 && input.value.length > 0) {
        e.style.backgroundColor = "#68B050";
    }
}