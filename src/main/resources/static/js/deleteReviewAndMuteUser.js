let deleteReviewAdmin = false;
let mute = false;
let isDeleted = false;
let isMuted = false;


function showOrHideDeleteReview(id, e) {
    deleteReviewAdmin = !deleteReviewAdmin;
    let confirmation = document.getElementById("delete");
    confirmation.hidden = !deleteReviewAdmin;
    if (deleteReviewAdmin) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isDeleted = false;
    }
}

function showOrHideMuteUser(id, e) {
    mute = !mute;
    let confirmation = document.getElementById("mute");
    confirmation.hidden = !mute;
    if (mute) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        isMuted = false;
    }
}

function muteUser() {
    if (!isMuted) {
        isMuted = true;
        let button = document.getElementById("buttonConfirmationMute");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("mute");
        fetch("/mute_user", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideMuteUser();
                if (content === "true") {
                    document.getElementById("success").textContent = document.getElementById("textSuccessMuteUser").textContent;
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })

    }
}

function deleteReview() {
    if (!isDeleted) {
        isDeleted = true;
        let button = document.getElementById("buttonConfirmationDelete");
        button.className = "grayButton";
        button.style.cursor = "wait";
        let confirmation = document.getElementById("delete");
        fetch("/delete_review", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
            })
        })
            .then(content => content.text())
            .then(content => {
                showOrHideDeleteReview();
                if (content === "true") {
                    location.reload();
                    document.getElementById("success").textContent = document.getElementById("textSuccessDeleteReview").textContent;
                } else {
                    document.getElementById("error").textContent = document.getElementById("textUnexpectedError").textContent;
                }
            })

    }
}