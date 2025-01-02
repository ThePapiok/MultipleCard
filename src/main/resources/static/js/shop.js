function checkPoints(type) {
    if (points != null) {
        for (let i = 0; i < points.length; i++) {
            let index;
            if (type === 0) {
                index = 12 + 7 * i;
            } else {
                index = 7 + 7 * i;
            }
            let address = points[i];
            let prefixAddress = "address[" + i + "].";
            addPlace(document.getElementById("plus"), address.street, address.houseNumber, address.apartmentNumber, address.postalCode, address.city, address.country, address.province, type);
            if (type === 0) {
                checkStreet(document.getElementsByName(prefixAddress + "street")[0], index++);
                checkHouseNumber(document.getElementsByName(prefixAddress + "houseNumber")[0], index++);
                checkApartmentNumber(document.getElementsByName(prefixAddress + "apartmentNumber")[0], index++);
                checkPostalCode(document.getElementsByName(prefixAddress + "postalCode")[0], index++);
                checkCity(document.getElementsByName(prefixAddress + "city")[0], index++);
                checkSelect(document.getElementsByName(prefixAddress + "country")[0], index++);
                checkProvince(document.getElementsByName(prefixAddress + "province")[0], index++);
            }
        }
    }
}

function addPlace(e, streetValue, houseNumberValue, apartmentNumberValue, postalCodeValue, cityValue, countryValue, provinceValue, type) {
    let hiddenValue;
    let placeholderApartmentNumber = document.getElementById("textApartmentNumber").textContent;
    let value = parseInt(e.previousElementSibling.textContent);
    if (value < 5) {
        const indexPlace = value;
        const incValue = value + 2;
        let index;
        let places = document.getElementById("places");
        let block = document.createElement("div");
        let placeSelect = document.createElement("div");
        let place = document.createElement("input");
        let minus = document.createElement("img");
        let form = document.createElement("div");
        if (type === 0) {
            ok.push(false, false, true, false, false, false, false);
            previous.push(false, false, true, false, false, false, false);
            check(11, false, ok, previous, buttonId, success, false, true);
            ok[10] = true;
            previous[10] = true;
            index = 10 + (value * 6) + (value + 1);
        } else if (type === 1) {
            ok.push(null, null, null, null, null, null, null);
            previous.push(null, null, null, null, null, null, null);
            index = 5 + (value * 6) + (value + 1);
            hiddenValue = "hidden";
            placeholderApartmentNumber = "";
        } else {
            ok.push(false, false, true, false, false, false, false);
            previous.push(false, false, true, false, false, false, false);
            check(6, false, ok, previous, buttonId, success, false, true);
            ok[5] = true;
            previous[5] = true;
            index = 5 + (value * 6) + (value + 1);
            const address = addresses[value];
            if (address != null && address.apartmentNumber === "") {
                hiddenValue = "hidden";
                placeholderApartmentNumber = "";
                const indexApartmentNumber = ok.length - 5;
                ok[indexApartmentNumber] = null;
                previous[indexApartmentNumber] = null;
            }
        }
        value++;
        e.previousElementSibling.textContent = value.toString();
        block.className = "block-horizontally block";
        placeSelect.id = "place" + value;
        placeSelect.className = "numberOfPlace";
        place.className = "place";
        place.value = document.getElementById("textPlace").textContent + " " + value;
        place.readOnly = true;
        place.onclick = () => showOrHideForm(form);
        block.appendChild(place);
        minus.src = "/images/minus.png";
        minus.alt = "minus";
        minus.title = document.getElementById("textDeletePlace").textContent;
        minus.className = "minus";
        minus.onclick = () => removePlace(placeSelect, type);
        block.appendChild(minus);
        placeSelect.appendChild(block);
        places.appendChild(placeSelect);
        form.className = "formAddress";
        form.innerHTML = `
        <div class="block-horizontally">
        <label id="Street">
        <label class="inputs">
          <input name="address[` + indexPlace + `].street" type="text" placeholder="` + document.getElementById("textStreet").textContent + `
           " required oninput="checkStreet(this, ` + ++index + `, ` + indexPlace + `, ` + type + `)"
                 minlength="2" maxlength="40" onmouseenter="showValidation(this, 'Street')"
                 onmouseleave="hideValidation('Street')" value="` + streetValue + `">
          <img ` + hiddenValue + ` id="close` + index + `" src="/images/close.png" alt="close">
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
                 oninput="checkHouseNumber(this, ` + ++index + `, ` + indexPlace + `, ` + type + `)" minlength="1" maxlength="10"
                 onmouseenter="showValidation(this, 'HouseNumber')"
                 onmouseleave="hideValidation('HouseNumber')" value="` + houseNumberValue + `">
          <img ` + hiddenValue + ` id="close` + index + `" src="/images/close.png" alt="close">
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
          <input name="address[` + indexPlace + `].apartmentNumber" type="text" placeholder="` + placeholderApartmentNumber + `"
                 oninput="checkApartmentNumber(this, ` + ++index + `, ` + indexPlace + `, ` + type + `)" maxlength="6"
                 onmouseenter="showValidation(this, 'ApartmentNumber')"
                 onmouseleave="hideValidation('ApartmentNumber')" value="` + apartmentNumberValue + `">
          <img hidden id="close` + index + `" src="/images/close.png" alt="close">
          <img ` + hiddenValue + ` id="check` + index + `" src="/images/check.png" alt="check">
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
                 oninput="checkPostalCode(this, ` + ++index + `, ` + indexPlace + `, ` + type + `)" maxlength="6" minlength="6"
                 onmouseenter="showValidation(this, 'PostalCode')"
                 onmouseleave="hideValidation('PostalCode')" value="` + postalCodeValue + `">
          <img ` + hiddenValue + ` id="close` + index + `" src="/images/close.png" alt="close">
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
                 oninput="checkCity(this, ` + ++index + `, ` + indexPlace + `, ` + type + `)"
                 minlength="2" maxlength="40" onmouseenter="showValidation(this, 'City')"
                 onmouseleave="hideValidation('City')" value="` + cityValue + `">
          <img ` + hiddenValue + ` id="close` + index + `" src="/images/close.png" alt="close">
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
                 placeholder="` + document.getElementById("textCountry").textContent + `" onclick="showOrHideCountry(` + incValue + `, ` + ++index + `)" value="` + countryValue + `">
          <img ` + hiddenValue + ` id="down` + index + `" src="/images/keyboard_down.png" alt="down">
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
                 oninput="checkProvince(this, ` + ++index + `, ` + indexPlace + `, ` + type + `)" minlength="2" maxlength="40"
                 onmouseenter="showValidation(this, 'Province')" onmouseleave="hideValidation('Province')" value="` + provinceValue + `">
          <img ` + hiddenValue + ` id="close` + index + `" src="/images/close.png" alt="close">
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
        index--;
        let options = document.getElementById("optionsCountry" + incValue);
        for (let i = 0; i < list.length; i++) {
            const country = list[i];
            let option = document.createElement("li");
            option.className = "optionSelect optionCountry" + incValue;
            option.dataset.code = country.code;
            option.textContent = country.name + ' (' + country.code + ')';
            option.onclick = () => setValueCountry(option, incValue, index, indexPlace, type);
            options.appendChild(option);
        }
    }
}

function removePlace(e, type) {
    const value = parseInt(document.getElementById("numberPlace").textContent);
    let index = parseInt(e.id.charAt(5));
    const skip = (index - 1) * 7;
    let sibling = e.nextElementSibling;
    let inputsThis;
    let inputsSibling;
    let streetE;
    let houseNumberE;
    let apartmentNumberE;
    let postalCodeE;
    let cityE;
    let countryE;
    let provinceE;
    index--;
    document.getElementById("numberPlace").textContent = (value - 1).toString();
    while (sibling != null) {
        inputsThis = e.firstElementChild.nextElementSibling.getElementsByTagName("input");
        inputsSibling = sibling.firstElementChild.nextElementSibling.getElementsByTagName("input");
        if (type === 0) {
            for (let i = 0; i < inputsThis.length; i++) {
                inputsThis[i].value = inputsSibling[i].value;
            }
        } else {
            streetE = inputsThis[0];
            houseNumberE = inputsThis[1];
            apartmentNumberE = inputsThis[2];
            postalCodeE = inputsThis[3];
            cityE = inputsThis[4];
            countryE = inputsThis[5];
            provinceE = inputsThis[7];
            streetE.value = inputsSibling[0].value;
            checkStreet(streetE, 7 + (index + 1) * 7, index, type);
            houseNumberE.value = inputsSibling[1].value;
            checkHouseNumber(houseNumberE, 8 + (index + 1) * 7, index, type);
            apartmentNumberE.value = inputsSibling[2].value;
            checkApartmentNumber(apartmentNumberE, 9 + (index + 1) * 7, index, type);
            postalCodeE.value = inputsSibling[3].value;
            checkPostalCode(postalCodeE, 10 + (index + 1) * 7, index, type);
            cityE.value = inputsSibling[4].value;
            checkCity(cityE, 11 + (index + 1) * 7, index, type);
            countryE.value = inputsSibling[5].value;
            checkCountry(countryE, 12 + (index + 1) * 7, index, type);
            inputsThis[6].value = inputsSibling[6].value;
            provinceE.value = inputsSibling[7].value;
            checkProvince(provinceE, 13 + (index + 1) * 7, index, type);
        }
        index++;
        e = sibling;
        sibling = sibling.nextElementSibling;
    }
    e.remove();
    if (type === 0) {
        ok.splice(11 + skip, 7);
        previous.splice(11 + skip, 7);
        if (document.getElementsByClassName("numberOfPlace").length === 0) {
            check(11, false, ok, previous, buttonId, success, false, true);
        } else {
            previous[10] = false;
            check(11, true, ok, previous, buttonId, success, false, true);
        }
    } else {
        ok.splice(6 + skip, 7);
        previous.splice(6 + skip, 7);
        if (document.getElementsByClassName("numberOfPlace").length === 0) {
            check(6, false, ok, previous, buttonId, success, false, true);
        } else {
            previous[5] = false;
            check(6, true, ok, previous, buttonId, success, false, true);
            deleteCheck(6, false, document.getElementsByName("address[0].street")[0], 1);
        }
    }

}

function showOrHideForm(e) {
    e.hidden = !e.hidden;
}
