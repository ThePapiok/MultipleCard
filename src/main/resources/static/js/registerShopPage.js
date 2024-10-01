const buttonId = "nextButton";
const regAccountNumber = new RegExp("^[0-9]*$");
let ok = [false, false, false, false, false, false, false, false, false, false, false];
let previous = [false, false, false, false, false, false, false, false, false, false, false];

function atStart() {
    checkFirstName(document.getElementById("firstName"), 1);
    checkLastName(document.getElementById("lastName"), 2);
    checkEmail(document.getElementById("email"), 3);
    checkCallingCode(document.getElementById("valueCallingCode"), 4);
    checkShopName(document.getElementById("name"));
    checkAccountNumber(document.getElementById("accountNumber"));
    for(let i = 0; i < points.length; i++){
        let index = 12 + 7 * i;
        let address = points[i];
        let prefixAddress = "address[" + i + "].";
        addPlace(document.getElementById("plus"), address.street, address.houseNumber, address.apartmentNumber, address.postalCode, address.city, address.country, address.province);
        checkStreet(document.getElementsByName(prefixAddress + "street")[0], index++);
        checkHouseNumber(document.getElementsByName(prefixAddress + "houseNumber")[0], index++);
        checkApartmentNumber(document.getElementsByName(prefixAddress + "apartmentNumber")[0], index++);
        checkPostalCode(document.getElementsByName(prefixAddress + "postalCode")[0], index++);
        checkCity(document.getElementsByName(prefixAddress + "city")[0], index++);
        checkSelect(document.getElementsByName(prefixAddress + "country")[0], index++);
        checkProvince(document.getElementsByName(prefixAddress + "province")[0], index++);
    }
    checkLanguage();
}

function checkShopName(e) {
    const input = e.value;
    check(8, (input.length >= 2 && input.length <= 30), ok, previous, buttonId, success, true, true);
}

function checkAccountNumber(e) {
    const input = e.value;
    check(9, (input.length === 26 && regAccountNumber.test(input)), ok, previous, buttonId, success, true, true);
}

function addPlace(e, streetValue, houseNumberValue, apartmentNumberValue, postalCodeValue, cityValue, countryValue, provinceValue) {
    let value = parseInt(e.previousElementSibling.textContent);
    if (value < 5) {
        const indexPlace = value;
        const incValue = value + 2;
        let index = 10 + (value * 6) + (value + 1);
        let places = document.getElementById("places");
        let block = document.createElement("div");
        let placeSelect = document.createElement("div");
        let place = document.createElement("input");
        let minus = document.createElement("img");
        let form = document.createElement("div");
        value++;
        ok.push(false, false, true, false, false, false, false);
        previous.push(false, false, true, false, false, false, false);
        check(11, false, ok, previous, buttonId, success, false, true);
        ok[10] = true;
        previous[10] = true;
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
        minus.className = "minus";
        minus.onclick = () => removePlace(placeSelect);
        block.appendChild(minus);
        placeSelect.appendChild(block);
        places.appendChild(placeSelect);
        form.className = "formAddress";
        form.innerHTML = `
        <div class="block-horizontally">
        <label id="Street">
        <label class="inputs">
          <input name="address[` + indexPlace + `].street" type="text" placeholder="` + document.getElementById("textStreet").textContent + `
           " required oninput="checkStreet(this, ` + ++index + `)"
                 minlength="2" maxlength="40" onmouseenter="showValidation(this, 'Street')"
                 onmouseleave="hideValidation('Street')" value="` + streetValue +`">
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
                 onmouseleave="hideValidation('HouseNumber')" value="` + houseNumberValue + `">
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
                 onmouseleave="hideValidation('ApartmentNumber')" value="` + apartmentNumberValue + `">
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
                 onmouseleave="hideValidation('PostalCode')" value="` + postalCodeValue + `">
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
                 onmouseleave="hideValidation('City')" value="` + cityValue + `">
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
                 placeholder="` + document.getElementById("textCountry").textContent + `" onclick="showOrHideCountry(` + incValue + `, ` + ++index + `)" value="` + countryValue + `">
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
                 onmouseenter="showValidation(this, 'Province')" onmouseleave="hideValidation('Province')" value="` + provinceValue + `">
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
        index--;
        let options = document.getElementById("optionsCountry" + incValue);
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
    const skip = (parseInt(e.id.charAt(5)) - 1) * 7;
    let sibling = e.nextElementSibling;
    document.getElementById("numberPlace").textContent = (value - 1).toString();
    while (sibling !== null) {
        let inputsThis = e.firstElementChild.nextElementSibling.getElementsByTagName("input");
        let inputsSibling = sibling.firstElementChild.nextElementSibling.getElementsByTagName("input");
        for (let i = 0; i < inputsThis.length; i++) {
            inputsThis[i].value = inputsSibling[i].value;
        }
        e = sibling;
        sibling = sibling.nextElementSibling;
    }
    e.remove();
    for (let i = 1; i <= 7; i++) {
        ok.splice(10 + skip + i);
        previous.splice(10 + skip + i);
    }
    if (document.getElementsByClassName("numberOfPlace").length === 0) {
        check(11, false, ok, previous, buttonId, success, false, true);
    } else {
        previous[10] = false;
        check(11, true, ok, previous, buttonId, success, false, true);
    }
}

function showOrHideForm(e) {
    e.hidden = !e.hidden;
}

function setImage(e) {
    let upload = document.getElementById("noImage");
    let image = e.files[0];
    let message;
    let error = false;
    const reader = new FileReader();
    const imagePreview = document.getElementById('image');
    if (image == null) {
        error = true;
        message = "";
    } else if (!image.type.startsWith("image")) {
        error = true;
        message = document.getElementById("textBadType").textContent;
    } else if (image.size >= 2000000) {
        error = true;
        message = document.getElementById("textTooBig").textContent;
    }
    if (error) {
        imagePreview.src = "/images/nothing.png";
        upload.hidden = false;
        check(10, false, ok, previous, buttonId, success, false, true);
        document.getElementById("errorFile").textContent = message;
        e.value = "";
        return;
    }
    reader.readAsDataURL(image);
    reader.onload = function (file) {
        let img = new Image();
        img.src = file.target.result;
        img.onload = function () {
            if (this.width < 450 || this.height < 450) {
                imagePreview.src = "/images/nothing.png";
                upload.hidden = false;
                check(10, false, ok, previous, buttonId, success, false, true);
                document.getElementById("errorFile").textContent = document.getElementById("textTooSmall").textContent;
                e.value = "";
                error = true;
            } else {
                imagePreview.src = img.src;
                upload.hidden = true;
                check(10, true, ok, previous, buttonId, success, false, true);
                document.getElementById("errorFile").textContent = "";
            }
        }
    };
}
