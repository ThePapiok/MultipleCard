function deleteLike(id, e) {
    fetch("/reviews/deleteLike", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id
        })
    })
        .then(response => response.json())
        .then(response => {
            if (response === true) {
                let amount = e.parentElement.firstElementChild;
                amount.dataset.isAdded = "0";
                amount.textContent = (parseInt(amount.textContent) - 1).toString();
            }
        })
        .catch((error) => {
            console.error(error);
        });
}

function addLike(id, e) {
    fetch("/reviews/addLike", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id
        })
    })
        .then(response => response.json())
        .then(response => {
            if (response === true) {
                let amount = e.parentElement.firstElementChild;
                amount.dataset.isAdded = "1";
                amount.textContent = (parseInt(amount.textContent) + 1).toString();
            }
        })
        .catch((error) => {
            console.error(error);
        });
}

function removeReview(id, e) {
    fetch("/reviews", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "id": id
        })
    })
        .then(response => response.json())
        .then(response => {
            if (response === true) {
                e.parentElement.parentElement.parentElement.remove();
            }
        })
        .catch((error) => {
            console.error(error);
        });
}