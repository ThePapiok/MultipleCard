const buttonId = "nextButton";
let ok = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, true, false, false, false, false, false, false, false];

function atStart() {
    checkFirstName(document.getElementById("firstName"), 1);
    checkLastName(document.getElementById("lastName"), 2);
    checkEmail(document.getElementById("email"), 3);
    checkProvince(document.getElementById("provinceInput"), 4);
    checkStreet(document.getElementById("streetInput"), 5);
    checkHouseNumber(document.getElementById("houseNumberInput"), 6);
    checkApartmentNumber(document.getElementById("apartmentNumberInput"), 7);
    checkPostalCode(document.getElementById("postalCodeInput"), 8);
    checkCity(document.getElementById("cityInput"), 9);
    checkSelect(document.getElementById("valueCountry"), 10)
    checkSelect(document.getElementById("valueCallingCode"), 11);
    checkPhone(document.getElementById("phone"), 12);
    document.getElementById("searchCallingCode").value = "";
    document.getElementById("searchCountry").value = "";
    checkLanguage();
}
