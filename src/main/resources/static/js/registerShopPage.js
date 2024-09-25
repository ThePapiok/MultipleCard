const buttonId = "nextButton";

let previousPostalCode = "";
let ok = [false, false, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, false, false, false];

function atStart() {
    checkLanguage();
}

function checkFirstName(e) {
    const input = e.value;
    check(1, (input.length >= 2 && input.length <= 15 && regFirstName.test(input)), ok, previous, buttonId, success, true, true);
}

function checkLastName(e) {
    const input = e.value;
    check(2, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkEmail(e) {
    const input = e.value;
    check(3, (input.length >= 4 && input.length <= 30 && regEmail.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPhone(e) {
    const input = e.value.toString().replaceAll(" ", "");
    const callingCodeLength = document.getElementById("valueCallingCode").value.length;
    check(5, (input.length >= 7 && input.length + callingCodeLength <= 16 && regPhone.test(input)), ok, previous, buttonId, success, true, true);
}

function checkPassword(e) {
    const input = e.value;
    check(6, (input.length >= 6 && input.length <= 25 && regPassword.test(input)), ok, previous, buttonId, success, true, true);
    checkRetypedPassword(document.getElementById("retypedPassword"));
}

function checkRetypedPassword(e) {
    check(7, (e.value === document.getElementById("password").value), ok, previous, buttonId, success, true, true);
}

function checkShopName(e) {
    const input = e.value;
    check(8, (input.length >= 2 && input.length <= 30), ok, previous, buttonId, success, true, true);
}

function setValueCallingCode(e) {
    let valueSelect = document.getElementById("valueCallingCode");
    valueSelect.value = e.dataset.value;
    checkSelect(valueSelect, 4);
    checkPhone(document.getElementById("phone"));
}

function checkSelect(e, index) {
    const input = e.value;
    check(index, (input !== ''), ok, previous, buttonId, success, false, true);
}

function addPlace(e) {
    let value = parseInt(e.previousElementSibling.textContent);
    if (value < 5) {
        let index = 8 + (value * 6) + (value + 1);
        const indexPlace = value;
        value++;
        const incValue = value + 1;
        ok.push(false, false, true, false, false, false, false);
        previous.push(false, false, true, false, false, false, false);
        check(9, false, ok, previous, buttonId, success, false, true);
        ok[8] = true;
        previous[8] = true;
        e.previousElementSibling.textContent = value.toString();
        let places = document.getElementById("places");
        let block = document.createElement("div");
        let placeSelect = document.createElement("div");
        block.className = "block-horizontally block";
        placeSelect.id = "place" + value;
        placeSelect.className = "numberOfPlace";
        let place = document.createElement("input");
        place.className = "place";
        place.value = document.getElementById("textPlace").textContent + " " + value;
        place.readOnly = true;
        place.onclick = () => showOrHideForm(form);
        block.appendChild(place);
        let minus = document.createElement("img");
        minus.src = "/images/minus.png";
        minus.alt = "minus";
        minus.className = "minus";
        minus.onclick = () => removePlace(placeSelect);
        block.appendChild(minus);
        placeSelect.appendChild(block);
        places.appendChild(placeSelect);
        let form = document.createElement("div");
        form.className = "formAddress";
        form.innerHTML = `
        <div class="block-horizontally">
        <label id="Street">
        <label class="inputs">
          <input name="address[` + indexPlace + `].street" type="text" placeholder="` + document.getElementById("textStreet").textContent + `
           " required oninput="checkStreet(this, ` + ++index + `)"
                 minlength="2" maxlength="40" onmouseenter="showValidation(this, 'Street')"
                 onmouseleave="hideValidation('Street')">
          <img id="close` + index + `" src="/images/close.png" alt="close">
          <img hidden id="check` + index + `" src="/images/check.png" alt="check">
        </label>
      </label>
      <div class="validation">
        <span>` + document.getElementById("textStreet").textContent + ' ' + document.getElementById("textValidation").textContent + `"</span>
        <ul>
          <li>` + document.getElementById("textValidationPartUppercase").textContent + `</li>
          <li>` + document.getElementById("textValidationOnlyLetters").textContent + `</li>
          <li>` + document.getElementById("textValidationMax2Parts").textContent + `</li>
          <li>` + document.getElementById("textValidationPrefixLength").textContent + " 2 " + document.getElementById("textValidationSuffixLength").textContent + ` 40</li>
        </ul>
      </div>
      <label id="HouseNumber">
        <label class="inputs">
          <input name="address[` + indexPlace + `].houseNumber" type="text" placeholder="` + document.getElementById("textHouseNumber").textContent + `" required
                 oninput="checkHouseNumber(this, ` + ++index + `)" minlength="1" maxlength="10"
                 onmouseenter="showValidation(this, 'HouseNumber')"
                 onmouseleave="hideValidation('HouseNumber')">
          <img id="close` + index + `" src="/images/close.png" alt="close">
          <img hidden id="check` + index + `" src="/images/check.png" alt="check">
        </label>
      </label>
      <div class="validation" id="validationHouseNumber">
        <span>` + document.getElementById("textHouseNumber").textContent + ' ' + document.getElementById("textValidation").textContent + `</span>
        <ul>
          <li>` + document.getElementById("textHouseNumberValidation1").textContent + `</li>
          <li>` + document.getElementById("textHouseNumberValidation2").textContent + `</li>
          <li>` + document.getElementById("textValidationPrefixLength").textContent + " 1 " + document.getElementById("textValidationSuffixLength").textContent + ` 10</li>
        </ul>
      </div>
        <label id="ApartmentNumber">
        <label class="inputs">
          <input name="address[` + indexPlace + `].apartmentNumber" type="text" placeholder="` + document.getElementById("textApartmentNumber").textContent + `"
                 oninput="checkApartmentNumber(this, ` + ++index + `)" maxlength="6"
                 onmouseenter="showValidation(this, 'ApartmentNumber')"
                 onmouseleave="hideValidation('ApartmentNumber')">
          <img hidden id="close` + index + `" src="/images/close.png" alt="close">
          <img id="check` + index + `" src="/images/check.png" alt="check">
        </label>
      </label>
      <div class="validation" id="validationApartmentNumber">
        <span>` + document.getElementById("textApartmentNumber").textContent + ' ' + document.getElementById("textValidation").textContent + `</span>
        <ul>
          <li>` + document.getElementById("textApartmentNumberValidation1").textContent + `</li>
          <li>` + document.getElementById("textValidationOnlyDigits").textContent + `</li>
          <li>` + document.getElementById("textApartmentNumberValidation2").textContent + `</li>
          <li>` + document.getElementById("textValidationPrefixLength").textContent + " 1 " + document.getElementById("textValidationSuffixLength").textContent + ` 6</li>
        </ul>
      </div>
    </div>
    <div class="block-horizontally">
      <label id="PostalCode">
        <label class="inputs">
          <input name="address[` + indexPlace + `].postalCode" type="text" placeholder="` + document.getElementById("textPostalCode").textContent + `" required
                 oninput="checkPostalCode(this, ` + ++index + `)" maxlength="6" minlength="6"
                 onmouseenter="showValidation(this, 'PostalCode')"
                 onmouseleave="hideValidation('PostalCode')">
          <img id="close` + index + `" src="/images/close.png" alt="close">
          <img hidden id="check` + index + `" src="/images/check.png" alt="check">
        </label>
      </label>
      <div class="validation">
        <span>` + document.getElementById("textPostalCode").textContent + ' ' + document.getElementById("textValidation").textContent + `</span>
        <ul>
          <li>` + document.getElementById("textValidationOnlyDigits").textContent + `</li>
          <li>` + document.getElementById("textValidationPrefixLengthIs").textContent + ` 5</li>
        </ul>
      </div>
       <label id="City">
        <label class="inputs">
          <input name="address[` + indexPlace + `].city" type="text" placeholder="` + document.getElementById("textCity").textContent + `" required
                 oninput="checkCity(this, ` + ++index + `)"
                 minlength="2" maxlength="40" onmouseenter="showValidation(this, 'City')"
                 onmouseleave="hideValidation('City')">
          <img id="close` + index + `" src="/images/close.png" alt="close">
          <img hidden id="check` + index + `" src="/images/check.png" alt="check">
        </label>
      </label>
      <div class="validation" id="validationCity">
        <span>` + document.getElementById("textCity").textContent + ' ' + document.getElementById("textValidation").textContent + `</span>
        <ul>
          <li>` + document.getElementById("textValidationPartUppercase").textContent + `</li>
          <li>` + document.getElementById("textValidationOnlyLetters").textContent + `</li>
          <li>` + document.getElementById("textValidationMax2Parts").textContent + `</li>
          <li>` + document.getElementById("textValidationPrefixLength").textContent + " 2 " + document.getElementById("textValidationSuffixLength").textContent + ` 40</li>
        </ul>
      </div>
    </div>
    <div class="block-horizontally">
      <label id="Country">
        <label class="inputs">
          <input name="address[` + indexPlace + `].country" type="text" id="valueCountry` + incValue + `" class="valueSelect" type="text" readonly
                 placeholder="` + document.getElementById("textCountry").textContent + `" onclick="showOrHideCountry(` + incValue + `, ` + ++index + `)" value="">
          <img id="down` + index + `" src="/images/keyboard_down.png" alt="down">
          <img hidden id="up` + index + `" src="/images/keyboard_up.png" alt="up">
        </label>
        <input id="searchCountry` + incValue + `" class="searchSelect" type="text" oninput="searchAll(this, true, ` + incValue + `)">
        <ul id="optionsCountry` + incValue + `" class="optionsSelect">
          <li hidden id="nothingCountry` + incValue + `">` + document.getElementById("textNoResults").textContent + `</li>
        </ul>
      </label>
      <label id="Province">
        <label class="inputs">
          <input name="address[` + indexPlace + `].province" type="text" placeholder="` + document.getElementById("textProvince").textContent + `" required
                 oninput="checkProvince(this, ` + ++index + `)" minlength="2" maxlength="40"
                 onmouseenter="showValidation(this, 'Province')" onmouseleave="hideValidation('Province')">
          <img id="close` + index + `" src="/images/close.png" alt="close">
          <img hidden id="check` + index + `" src="/images/check.png" alt="check">
        </label>
      </label>
      <div class="validation" id="validationProvince">
        <span>` + document.getElementById("textProvince").textContent + ' ' + document.getElementById("textValidation").textContent + `</span>
        <ul>
          <li>` + document.getElementById("textValidationPartUppercase").textContent + `</li>
          <li>` + document.getElementById("textValidationOnlyLetters").textContent + `</li>
          <li>` + document.getElementById("textValidationMax2Parts").textContent + `</li>
          <li>` + document.getElementById("textValidationPrefixLength").textContent + " 2 " + document.getElementById("textValidationSuffixLength").textContent + ` 40</li>
        </ul>
      </div>
    </div>
        `
        placeSelect.appendChild(form);
        let options = document.getElementById("optionsCountry" + incValue);
        index--;
        for (let i = 0; i < list.length; i++) {
            const country = list[i];
            let option = document.createElement("li");
            option.className = "optionSelect optionCountry" + incValue;
            option.dataset.code = country.code;
            option.textContent = country.name + ' (' + country.code + ')';
            option.onclick = () => setValueCountry(option, incValue, index);
            options.appendChild(option);
        }
    }
}

function removePlace(e) {
    const value = parseInt(document.getElementById("numberPlace").textContent);
    document.getElementById("numberPlace").textContent = (value - 1).toString();
    let sibling = e.nextElementSibling;
    while (sibling !== null) {
        let inputsThis = e.firstElementChild.nextElementSibling.getElementsByTagName("input");
        let inputsSibling = sibling.firstElementChild.nextElementSibling.getElementsByTagName("input");
        for (let i = 0; i < inputsThis.length; i++) {
            inputsThis[i].value = inputsSibling[i].value;
        }
        e = sibling;
        sibling = sibling.nextElementSibling;
    }
    const skip = (parseInt(e.id.charAt(5)) - 1) * 7;
    e.remove();
    for (let i = 1; i <= 7; i++) {
        ok.splice(8 + skip + i);
        previous.splice(8 + skip + i);
    }
    if (document.getElementsByClassName("numberOfPlace").length === 0) {
        check(9, false, ok, previous, buttonId, success, false, true);
    }
}

function showOrHideForm(e) {
    e.hidden = !e.hidden;
}

function setValueCountry(e, incValue, value) {
    let valueSelect = document.getElementById("valueCountry" + incValue);
    valueSelect.value = e.textContent;
    checkSelect(valueSelect, value);
}

function checkStreet(e, value) {
    const input = e.value;
    check(value, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkHouseNumber(e, value) {
    const input = e.value;
    check(value, (input.length >= 1 && input.length <= 10 && regHouseNumber.test(input)), ok, previous, buttonId, success, true, true);
}

function checkApartmentNumber(e, value) {
    const input = e.value;
    check(value, ((input >= 1 && input <= 10000 && regApartmentNumber.test(input)) || input.length === 0), ok, previous, buttonId, success, true, true);
}

function checkPostalCode(e, value) {
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
    check(value, (regPostalCode.test(input)), ok, previous, buttonId, success, true, true);
}

function checkCity(e, value) {
    const input = e.value;
    check(value, (input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}

function checkProvince(e, value) {
    const input = e.value;
    check(value, (input.length >= 2 && input.length <= 40 && regLastNameAndCity.test(input)), ok, previous, buttonId, success, true, true);
}


