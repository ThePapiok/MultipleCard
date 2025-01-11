let report = false;
let hasText = false;

function showOrHideReportProduct(id, e) {
    report = !report;
    let confirmation = document.getElementById("report");
    confirmation.hidden = !report;
    if (report) {
        const cord = e.getBoundingClientRect();
        confirmation.style.left = cord.left + window.scrollX + 'px';
        confirmation.style.top = cord.top + window.scrollY + 'px';
        confirmation.dataset.id = id;
    } else {
        confirmation.dataset.id = "";
        document.getElementById("buttonConfirmation").className = "grayButton";
        document.getElementById("reportMessage").value = "";
        hasText = false;
    }
}

function reportProduct(isProduct) {
    if (hasText) {
        let isOk = false;
        let confirmation = document.getElementById("report");
        fetch("/report", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "id": confirmation.dataset.id,
                "description": document.getElementById("reportMessage").value,
                "isProduct": isProduct
            })
        })
            .then(content => {
                console.log(content.status)
                if (content.status === 200) {
                    isOk = true;
                }
                return content.text().then(text => ({isOk, text}));
            })
            .then(({isOk, text}) => {
                showOrHideReportProduct();
                if (isOk) {
                    document.getElementById("success").textContent = text;
                } else {
                    document.getElementById("error").textContent = text;
                }
            })
            .catch(error => {
                console.error(error);
            })
    }
}

function checkTextLength(e) {
    const length = e.value.length;
    if (length >= 10 && length <= 1000) {
        document.getElementById("buttonConfirmation").className = "darkGreenButton";
        hasText = true;
    } else {
        document.getElementById("buttonConfirmation").className = "grayButton";
        hasText = false;
    }
}
