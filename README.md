
# Multiple Card

Aplikacja webowa pozwalająca na korzystanie z wielkrotynych kart podarunkowych. Kartę można doładować kupując na niej jakiś produkt, a następnie w sklepie można ten produkt dostać.


![Logo](https://i.imgur.com/C5RsVmp.png)

## Linki

- [Strona produkcyjna](https://multiplecard-neq8.onrender.com/)


## Zmienne Środowiskowe

Aby projekt działał potrzebuje on dodania zmiennych środowiskowych:

`MONGODB_HOST`

`MONGODB_USERNAME`

`MONGODB_PASSWORD`

`MONGODB_PRE` - mongodb lub mongodb+srv

`AUTH_TOKEN` - token z api twilio

`PHONE_NUMBER` - numer telefonu z api twilio

`ACCOUNT_SID` - account sid z api twilio





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
  export PHONE_NUMBER=+123123123123
  export ACCOUNT_SID=accountSidTest
  export AUTH_TOKEN=authTokenTest
```

Uruchom kontenery

```bash
  docker-compose up
```

Wejdź na adres **[localhost:8080](http://localhost:8080/)**



## Uruchomienie testów

Aby uruchomić testy, użyj tej komendy

```bash
  mvn test
```


 

