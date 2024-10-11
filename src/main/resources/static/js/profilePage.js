let ok = [null, null, null, null, null, null, null, null, null];
let previous = [null, null, null, null, null, null, null, null, null];
const buttonId = "saveButton";


function atStart() {
    firstName = document.getElementById("firstName").value;
    lastName = document.getElementById("lastName").value;
    addresses.push(new address(
        document.getElementById("streetInput").value,
        document.getElementById("houseNumberInput").value,
        document.getElementById("apartmentNumberInput").value,
        document.getElementById("postalCodeInput").value,
        document.getElementById("cityInput").value,
        document.getElementById("valueCountry").value,
        document.getElementById("provinceInput").value
    ));
    if (document.getElementById("cardNotFound") != null) {
        document.getElementById("card").style.opacity = "50%";
    }
    checkLanguage();
}
