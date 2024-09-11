const regFirstName = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$");
const regLastNameAndCity = new RegExp("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$");
const regHouseNumber = new RegExp("^[1-9][0-9]*([A-Z]|\/[1-9][0-9]*)?$");
const regApartmentNumber = new RegExp("^[1-9][0-9]*$");
const regPostalCode = new RegExp("^[0-9]{2}-[0-9]{3}$");