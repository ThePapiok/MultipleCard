# Multiple Card

Aplikacja webowa pozwalająca na korzystanie z wielokrotnych kart podarunkowych. Kartę można doładować, kupując na niej
jakiś produkt, a następnie w sklepie można ten produkt dostać.

![Logo](https://i.imgur.com/3d9NnZt.png)

## Linki

- [Strona produkcyjna](https://multiplecard-neq8.onrender.com/)

## Technologie

- Java 17
- Spring boot 3.3.2
- Mongodb
- Twilio
- Spring security
- Spring mail
- Spring aop
- Thymeleaf
- Cloudinary
- Zxing
- IbanApi
- PayU
- Checkstyle
- Spotless
- Google Maps

## Zmienne Środowiskowe

Aby projekt działał, potrzebuje on dodania zmiennych środowiskowych oraz ustawić profil na dev:

`MONGODB_HOST`

`MONGODB_USERNAME`

`MONGODB_PASSWORD`

`MONGODB_PRE` - mongodb lub mongodb+srv

`TWILIO_AUTH_TOKEN` - token z api twilio

`TWILIO_MESSAGING_SERVICE_SID` - messaging service sid z api twilio

`TWILIO_ACCOUNT_SID` - account sid z api twilio

`GMAIL_APP_PASSWORD` - hasło aplikacji gmail

`CLOUDINARY_CLOUD_NAME` - nazwa dysku cloudinary

`CLOUDINARY_API_KEY` - api key dla cloudinary

`CLOUDINARY_API_SECRET` - api secret dla cloudinary

`IBANAPI_API_KEY` - api key dla ibanapi

`PAYU_CLIENT_SECRET` - client secret dla PayU

`PAYU_CLIENT_ID` - client id dla PayU

`PAYU_KEY_MD5` - klucz md5 dla PayU

`GOOGLE_API_KEY` - klucz api dla Google maps

## Uruchomienie z dockerem

Sklonuj projekt

```bash
  git clone https://github.com/ThePapiok/MultipleCard.git
```

Wejdź do uzyskanego folderu

```bash
  cd MultipleCard
```

Dodaj zmienne środowiskowe (przykładowe)

```bash
  export TWILIO_MESSAGING_SERVICE_SID=twilioServiceSidTest
  export TWILIO_ACCOUNT_SID=twilioAccountSidTest
  export TWILIO_AUTH_TOKEN=twilioAuthTokenTest
  export GMAIL_APP_PASSWORD=gmailAppPasswordTest
  export CLOUDINARY_CLOUD_NAME=cloudinaryCloudNameTest
  export CLOUDINARY_API_KEY=cloudinaryApiKeyTest
  export CLOUDINARY_API_SECRET=cloudinaryApiSecretTest
  export IBANAPI_API_KEY=ibanapiApiKeyTest
```

Uruchom kontenery

```bash
  ./db.sh
  docker-compose up web
```

Wejdź na adres **[localhost:8080](http://localhost:8080/)**

Po skończeniu używania

```bash
  docker-compose down
```

## Uruchomienie testów

Aby uruchomić testy, użyj tej komendy (wymagane mongosh, docker, mvn)

```bash
    ./tests.sh
```



 

