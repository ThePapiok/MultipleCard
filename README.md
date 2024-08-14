
# Multiple Card

Aplikacja webowa pozwalająca na korzystanie z wielkrotynych kart podarunkowych. Kartę można doładować kupując na niej jakiś produkt, a następnie w sklepie można ten produkt dostać.


![Logo](https://i.imgur.com/4RCLq1L.png)

## Linki

- [Strona produkcyjna](https://multiplecard-neq8.onrender.com/)


## Zmienne Środowiskowe

Aby projekt działał potrzebuje on dodania zmiennych środowiskowych:

`MONGODB_HOST`

`MONGODB_USERNAME`

`MONGODB_PASSWORD`


## Uruchomienie z dockerem

Sklonuj projekt

```bash
  git clone https://github.com/ThePapiok/MultipleCard.git
```

Wejdź do uzyskanego folderu

```bash
  cd MultipleCard
```

Uruchom kontenery

```bash
  docker-compose up
```

Wejdź na adres

```bash
  localhost:8080
```


## Uruchomienie testów

Aby uruchomić testy, użyj tej komendy

```bash
  mvn test
```


 

