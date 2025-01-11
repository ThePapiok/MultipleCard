let showOptions = false;
let searchOption = "";

function atStart(maxPage, page) {
    checkLanguage();
    let selectedPage = document.getElementById("page" + page);
    if (selectedPage != null) {
        selectedPage.classList.add("selectedPage");
    }
    checkButtonPages(page, maxPage);
}

function showOrHideOptions(e, type, id, firstValue, secondValue) {
    const coords = e.getBoundingClientRect();
    const options = document.getElementById("options");
    options.style.width = e.offsetWidth.toString() + "px";
    let hidden = options.hidden;
    if (hidden && !showOptions && ((type === "role" && (e.textContent === "ROLE_ADMIN" || e.textContent === "ROLE_USER")) || (type !== "role")) && ((type === "restricted" && e.textContent !== "") || (type !== "restricted"))) {
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
    if (index === searchOption) {
        e.style.backgroundColor = "#dcdee0";
        searchOption = "";
    } else {
        if (searchOption !== "") {
            document.getElementById("cell" + searchOption).style.backgroundColor = "#dcdee0";
        }
        searchOption = index;
        e.style.backgroundColor = "#C1C3C4";
    }
}

function searchUsers(e) {
    const value = e.value;
    window.location = "/admin_panel?type=" + searchOption + "&value=" + value;
}
