# Multiple Card

Aplikacja webowa pozwalająca na korzystanie z wielkrotynych kart podarunkowych. Kartę można doładować kupując na niej
jakiś produkt, a następnie w sklepie można ten produkt dostać.

![Logo](https://i.imgur.com/C5RsVmp.png)

## Linki

- [Strona produkcyjna](https://multiplecard-neq8.onrender.com/)

## Zmienne Środowiskowe

Aby projekt działał potrzebuje on dodania zmiennych środowiskowych oraz ustawić profil na prod:

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
```

Uruchom kontenery

```bash
  docker-compose up
```

Wejdź na adres **[localhost:8080](http://localhost:8080/)**

Po skończeniu używania

```bash
  docker-compose down -v
```

## Uruchomienie testów

Aby uruchomić testy, użyj tej komendy

```bash
  docker-compose up db
```

```bash
  mvn test
```

Po skończeniu testów

```bash
  docker-compose down -v
```


 

