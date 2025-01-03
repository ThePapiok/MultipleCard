let block = false;
let isBlocked = false;


function showOrHideBlockUser(id, e) {
    block = !block;
    let confirmation = document.getElementById("block");
    confirmation.hidden = !block;
    if (block) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isBlocked = false;
    }
}

function blockUser(isProduct) {
    if (!isBlocked) {
        isBlocked = true;
        let button = document.getElementById("buttonConfirmationBlock");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("block");
        fetch("/block_user", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
                "isProduct": isProduct
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideBlockUser();
                if (content === "true") {
                    document.getElementById("success").textContent = document.getElementById("textSuccessBlockUser").textContent;
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })

    }
}