let showOptions = false;
let searchOption = -1;

function atStart() {
    checkLanguage();
}

function showOrHideOptions(e, type, id) {
    const coords = e.getBoundingClientRect();
    const options = document.getElementById("options");
    options.style.width = e.offsetWidth.toString() + "px";
    let hidden = options.hidden;
    if (hidden && !showOptions) {
        showOptions = true;
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

function changeToTrue(e) {
    const id = e.dataset.id;
    const type = e.dataset.type;
    fetch("/change_user", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id,
            "type": type,
            "value": true
        })
    })
        .then(content => content.text())
        .then(content => {
            const cell = document.getElementById(id);
            if (content) {
                if (type) {
                    const active = cell.getElementsByClassName("active")[0];
                    active.textContent = "true";
                } else {
                    const banned = cell.getElementsByClassName("banned")[0];
                    banned.textContent = "true";
                }
            } else {
                document.getElementById("error").textContent = document.getElementById("textErrorUnexpected").textContent;
            }
        })
        .catch(error => {
            console.error(error);
        });
}

function changeToFalse(e) {
    const id = e.dataset.id;
    const type = e.dataset.type;
    fetch("/change_user", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id,
            "type": type,
            "value": false
        })
    })
        .then(content => content.text())
        .then(content => {
            const cell = document.getElementById(id);
            if (content) {
                if (type) {
                    const active = cell.getElementsByClassName("active")[0];
                    active.textContent = "false";
                } else {
                    const banned = cell.getElementsByClassName("banned")[0];
                    banned.textContent = "false";
                }
            } else {
                document.getElementById("error").textContent = document.getElementById("textErrorUnexpected").textContent;
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