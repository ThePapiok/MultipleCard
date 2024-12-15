let category = true;
let shopName = true;

function showOrHideCategories(e) {
    category = !category;
    e.hidden = category;
}

function showOrHideShopNames(e) {
    shopName = !shopName;
    e.hidden = shopName;
}

function getCategories(categories, e) {
    let category;
    fetch("/get_categories", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "prefix": e.value
        })
    })
        .then(content => content.json())
        .then(content => {
            categories.innerHTML = "";
            for (let i = 0; i < content.length; i++) {
                category = document.createElement("li");
                category.className = "option";
                category.textContent = content[i];
                category.onclick = () => {
                    document.getElementById("categoryInput").value = content[i];
                    showOrHideShopNames(categories);
                };
                categories.appendChild(category);
            }
        })
        .catch(error => {
            console.error(error);
        })
}

function getShopNames(shopNames, e) {
    let shopName;
    fetch("/get_shop_names", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            "prefix": e.value
        })
    })
        .then(content => content.json())
        .then(content => {
            shopNames.innerHTML = "";
            for (let i = 0; i < content.length; i++) {
                shopName = document.createElement("li");
                shopName.className = "option";
                shopName.textContent = content[i];
                shopName.onclick = () => {
                    document.getElementById("shopNameInput").value = content[i];
                    showOrHideShopNames(shopNames);
                };
                shopNames.appendChild(shopName);
            }
        })
        .catch(error => {
            console.error(error);
        })
}