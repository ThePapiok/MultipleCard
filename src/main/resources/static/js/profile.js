class address {
    constructor(street, houseNumber, apartmentNumber, postalCode, city, country, province) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartmentNumber = apartmentNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.province = province;
    }
}
let firstName;
let lastName;
let addresses = [];

function checkFirstName(e, value) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)), value, firstName, true, e);
}

function checkLastName(e, value) {
    const input = e.value;
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), value, lastName, true, e);
}

function checkStreet(e, value, index, type) {
    let startInput;
    const input = e.value;
    if (addresses[index] != null) {
        startInput = addresses[index].street;
    }
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), value, startInput, true, e, type);
}

function checkHouseNumber(e, value, index, type) {
    let startInput;
    const input = e.value;
    if (addresses[index] != null) {
        startInput = addresses[index].houseNumber;
    }
    checkOnlyIfOther(input, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)), value, startInput, true, e, type);
}

function checkApartmentNumber(e, value, index, type) {
    let startInput;
    const input = e.value;
    if (addresses[index] != null) {
        startInput = addresses[index].apartmentNumber;
    }
    checkOnlyIfOther(input, ((input >= 1 && input <= 10000 && regNumber.test(input)) || input.length === 0), value, startInput, true, e, type);
}

function checkPostalCode(e, value, index, type) {
    let startInput;
    let input = e.value;
    let length = input.length;
    if (length === 2) {
        if (previousPostalCode.charAt(previousPostalCode.length - 1) === "-") {
            e.value = input.substring(0, length - 1);
        } else {
            e.value += "-";
        }
        input = e.value;
    }
    previousPostalCode = input;
    if (addresses[index] != null) {
        startInput = addresses[index].postalCode;
    }
    checkOnlyIfOther(input, ((regPostalCode.test(input)) || input.length === 0), value, startInput, true, e, type);
}

function checkCity(e, value, index, type) {
    let startInput;
    const input = e.value;
    if (addresses[index] != null) {
        startInput = addresses[index].city;
    }
    checkOnlyIfOther(input, (input.length <= 40 && regLastNameAndCity.test(input)), value, startInput, true, e, type);
}

function checkProvince(e, value, index, type) {
    let startInput;
    const input = e.value;
    if (addresses[index] != null) {
        startInput = addresses[index].province;
    }
    checkOnlyIfOther(input, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), value, startInput, true, e, type);
}

function checkCountry(e, value, index, type) {
    let startInput;
    const input = e.value;
    if (addresses[index] != null) {
        startInput = addresses[index].country;
    }
    checkOnlyIfOther(input, (input !== ''), value, startInput, false, e, type);
}

function setValueCountry(e, incValue, value, index, type) {
    let valueSelect = document.getElementById("valueCountry" + incValue);
    valueSelect.value = e.textContent;
    checkCountry(valueSelect, value, index, type);
}
