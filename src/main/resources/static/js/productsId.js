let productsId = new Map();

function addProductId(id, e) {
    let amount = parseInt(e.textContent);
    if (amount < 10) {
        amount++;
        e.textContent = amount.toString();
        if (!productsId.has(id)) {
            productsId.set(id, 1);
        } else {
            productsId.set(id, productsId.get(id) + 1);
        }
        return true;
    }
    return false;
}

function deleteProductId(id, e) {
    let amount = parseInt(e.textContent);
    let size;
    if (amount > 0) {
        amount--;
        e.textContent = amount.toString();
        if (productsId.has(id)) {
            size = productsId.get(id) - 1;
            if (size === 0) {
                productsId.delete(id);
            } else {
                productsId.set(id, size);
            }
        }
        return true;
    }
    return false;
}