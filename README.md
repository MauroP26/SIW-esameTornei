# SIW – Gestione tornei di calcio amatoriale

Applicazione universitaria realizzata con Spring Boot, Spring MVC, Spring Data JPA/Hibernate, PostgreSQL, Spring Security, Thymeleaf e React.

## Funzionalità

- consultazione pubblica di tornei, squadre, rose, partite, calendario e classifica;
- autenticazione con ruoli `USER` e `ADMIN`;
- commenti alle partite, modificabili ed eliminabili soltanto dall'autore;
- pannello amministrativo per tornei, squadre, giocatori, arbitri e partite;
- registrazione e correzione dei risultati;
- API REST pubbliche di sola lettura;
- frontend React per calendario e classifica;
- benchmark JPA su N+1, `JOIN FETCH` ed `EntityGraph`;
- test unitari e di integrazione con JUnit, Mockito, MockMvc e H2.

## Requisiti locali

- Java 17 o successivo;
- PostgreSQL;
- Node.js e npm soltanto per modificare o ricostruire il frontend React.

## Avvio rapido del backend

1. Creare un database PostgreSQL chiamato `tournament`.
2. Aprire PowerShell nella cartella che contiene `pom.xml`.
3. Impostare le credenziali del database:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/tournament"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="la_tua_password"
```

4. Avviare:

```powershell
.\mvnw spring-boot:run
```

Applicazione: `http://localhost:8080`

Il valore predefinito di `spring.jpa.hibernate.ddl-auto` è `update`. Può essere sovrascritto con `JPA_DDL_AUTO`.

## Account demo

Creati automaticamente quando il database non contiene tornei:

| Ruolo | Username | Password |
|---|---|---|
| ADMIN | `admin` | `admin` |
| USER | `mario` | `password` |

Sono credenziali esclusivamente dimostrative e vanno cambiate in un'eventuale distribuzione reale.

## Test automatici

I test usano H2 in memoria e non modificano PostgreSQL:

```powershell
.\mvnw clean test
```

Risultato atteso:

```text
Failures: 0
Errors: 0
BUILD SUCCESS
```

Dettagli in [`TESTING.md`](TESTING.md).

## Frontend React

Il progetto React si trova in `frontend-react`.

```powershell
cd frontend-react
npm install
npm run dev
```

Vite inoltra le richieste `/api` al backend su porta 8080.

Per produrre la build servita da Spring Boot:

```powershell
npm run build
```

La build viene scritta in `src/main/resources/static/react-app` ed è raggiungibile da `/react-app/index.html`.

Dettagli in [`GUIDA_REACT.md`](GUIDA_REACT.md).

## API REST

- `GET /api/tornei`
- `GET /api/tornei/{id}`
- `GET /api/tornei/{id}/partite`
- `GET /api/tornei/{id}/classifica`
- `GET /api/squadre/{id}`

Le API restituiscono DTO dedicati, non entità JPA.

## Benchmark JPA/Hibernate

```powershell
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=benchmark"
```

Il benchmark confronta:

- accesso `LAZY` con problema N+1;
- query con `JOIN FETCH`;
- query con `@EntityGraph`.

I risultati vengono salvati in `benchmark-results/`. Consultare [`GUIDA_BENCHMARK_JPA.md`](GUIDA_BENCHMARK_JPA.md) e [`RELAZIONE_PRESTAZIONI_JPA.md`](RELAZIONE_PRESTAZIONI_JPA.md).

## Struttura principale

```text
src/main/java/it/unirom3/siw/
├── api/          controller REST e DTO di risposta
├── benchmark/    esperimento prestazionale JPA
├── config/       inizializzazione dati demo
├── controller/   controller MVC e amministrativi
├── dto/          form DTO con validazione
├── exception/    eccezioni applicative
├── model/        entità e classi di dominio
├── repository/   repository Spring Data JPA
├── security/     autenticazione e autorizzazione
└── service/      logica applicativa e transazioni
```

## Documentazione per la consegna

- [`CHECKLIST_CONSEGNA.md`](CHECKLIST_CONSEGNA.md): controlli prima del caricamento su GitHub;
- [`GUIDA_ORALE.md`](GUIDA_ORALE.md): percorso consigliato per la presentazione;
- [`STATO_PROGETTO.md`](STATO_PROGETTO.md): riepilogo delle milestone;
- [`TESTING.md`](TESTING.md): strategia di test;
- [`GUIDA_BENCHMARK_JPA.md`](GUIDA_BENCHMARK_JPA.md): esperimento sulle prestazioni.
