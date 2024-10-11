const buttonId = "saveButton";
let ok = [null, null, null, null, null, null];
let previous = [null, null, null, null, null, null];

let name;
let accountNumber;


function atStart() {
    checkPoints(1);
    firstName = document.getElementById("firstName").value;
    lastName = document.getElementById("lastName").value;
    name = document.getElementById("nameInput").value;
    accountNumber = document.getElementById("accountNumberInput").value;
    let places = document.getElementsByClassName("numberOfPlace");
    let nextIndex;
    let inputs;
    for (let i = 0; i < places.length; i++) {
        inputs = places[i].firstElementChild.nextElementSibling.getElementsByTagName("input");
        addresses.push(new address(
            inputs[0].value,
            inputs[1].value,
            inputs[2].value,
            inputs[3].value,
            inputs[4].value,
            inputs[5].value,
            inputs[7].value
        ));
    }
    checkLanguage();
}


function checkShopName(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 30), 3, name, true, e);
}

function checkAccountNumber(e) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length === 26 && regAccountNumber.test(input)), 4, accountNumber, true, e);
}





