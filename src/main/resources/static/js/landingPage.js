let ok = [false]
let previous = [false]
let success = {value: false}

function setStars(startIndex, endIndex) {
    for (let i = startIndex; i <= endIndex; i++) {
        document.getElementById("checked-" + i).style.display = "inline";
        document.getElementById("unchecked-" + i).style.display = "none";
    }
}

function unsetStars(startIndex, endIndex) {
    for (let i = startIndex; i <= endIndex; i++) {
        document.getElementById("checked-" + i).style.display = "none";
        document.getElementById("unchecked-" + i).style.display = "inline";
    }
}

function setStar(index) {
    let rating = document.getElementById("rating");
    const ratingValue = parseInt(rating.value);
    if (index > ratingValue) {
        setStars(ratingValue + 1, index);
        rating.value = index;
    } else if (index < ratingValue) {
        unsetStars(index + 1, ratingValue);
        rating.value = index;
    } else {
        unsetStars(1, index);
        rating.value = 0;
    }
}

function atStart() {
    checkLanguage();
}

function checkReview(e) {
    const input = e.value;
    check(1, (input.length >= 1), ok, previous, "addOpinionButton", success, false);
}

