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
            if (response) {
                let yourReviews = document.getElementsByClassName("yourReview");
                let amount;
                let hearths;
                let reviews;
                for (let i = 0; i < yourReviews.length; i++) {
                    amount = yourReviews[i].nextElementSibling.firstElementChild;
                    if (i === 0) {
                        hearths = parseInt(amount.textContent) - 1;
                    }
                    amount.dataset.isAdded = "0";
                    amount.textContent = (hearths).toString();
                    reviews = yourReviews[i].parentElement.parentElement.parentElement.parentElement;
                    if (reviews.id === "yourReview") {
                        continue;
                    }
                    const review = yourReviews[i].parentElement.parentElement.parentElement;
                    const nextReview = review.nextElementSibling;
                    if (nextReview != null) {
                        const hearthsPreviews = parseInt(nextReview.firstElementChild.firstElementChild.lastElementChild.firstElementChild.textContent);
                        if (hearths < hearthsPreviews) {
                            reviews.insertBefore(nextReview, review);
                        }
                    }
                }
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
            if (response) {
                let yourReviews = document.getElementsByClassName("yourReview");
                let amount;
                let hearths;
                let reviews;
                for (let i = 0; i < yourReviews.length; i++) {
                    amount = yourReviews[i].nextElementSibling.firstElementChild;
                    if (i === 0) {
                        hearths = parseInt(amount.textContent) + 1;
                    }
                    amount.dataset.isAdded = "1";
                    amount.textContent = (hearths).toString();
                    reviews = yourReviews[i].parentElement.parentElement.parentElement.parentElement;
                    if (reviews.id === "yourReview") {
                        continue;
                    }
                    const review = yourReviews[i].parentElement.parentElement.parentElement;
                    const previousReview = review.previousElementSibling;
                    if (previousReview != null) {
                        const hearthsPreviews = parseInt(previousReview.firstElementChild.firstElementChild.lastElementChild.firstElementChild.textContent);
                        if (hearths > hearthsPreviews) {
                            reviews.insertBefore(review, previousReview);
                        }
                    }
                }
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
            if (response) {
                let yourReviews = document.getElementsByClassName("yourReview");
                while (yourReviews.length !== 0) {
                    yourReviews[0].parentElement.parentElement.parentElement.remove();
                }
                let reviews = document.getElementsByClassName("review");
                if (reviews.length === 0) {
                    document.getElementById("noResults").dataset.reviewSize = "0";
                }

            }
        })
        .catch((error) => {
            console.error(error);
        });
}