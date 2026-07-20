# Guida sintetica per l'orale

## 1. Presentazione del problema

L'applicazione gestisce tornei di calcio amatoriale: squadre, rose, arbitri, partite, risultati, calendario, classifica e commenti degli utenti.

## 2. Architettura

Mostrare il flusso:

```text
browser → controller → service → repository → PostgreSQL
```

Spiegare che:

- i controller gestiscono HTTP e binding dei form;
- i service contengono regole applicative e transazioni;
- i repository eseguono l'accesso ai dati;
- le entità rappresentano il dominio;
- i DTO separano input web, API ed entità persistenti.

## 3. Modello JPA

Evidenziare:

- `Torneo`–`Squadra` molti-a-molti;
- `Squadra`–`Giocatore` uno-a-molti;
- `Torneo`–`Partita` uno-a-molti;
- collegamenti di una partita con casa, trasferta e arbitro;
- `Utente`–`Credenziali` uno-a-uno;
- vincolo di un commento per coppia utente/partita.

Spiegare cascade, lato proprietario e caricamento `LAZY`.

## 4. Sicurezza

Mostrare:

- pagine e API pubbliche;
- autenticazione form login;
- ruoli `USER` e `ADMIN`;
- protezione di `/admin/**`;
- controllo applicativo dell'autore nei commenti.

Sottolineare che nascondere un pulsante nella vista non basta: l'autorizzazione viene verificata anche nel backend.

## 5. Classifica e regole di business

Descrivere il calcolo di punti, vittorie, pareggi, sconfitte, gol e differenza reti. Le partite `SCHEDULED` non concorrono alla classifica.

Citare esempi di regole:

- casa e trasferta devono essere diverse;
- entrambe devono partecipare al torneo;
- i gol non possono essere negativi;
- una squadra coinvolta in partite storiche non viene eliminata liberamente.

## 6. Thymeleaf e React

Thymeleaf copre l'applicazione MVC e i form amministrativi. React consuma le API REST per aggiornare dinamicamente calendario e classifica.

Spiegare perché le API usano record/DTO dedicati: contratto stabile, assenza di cicli JSON e nessuna esposizione involontaria delle relazioni JPA.

## 7. Esperimento JPA

Mostrare i tre metodi del repository:

- caricamento `LAZY`;
- `JOIN FETCH`;
- `@EntityGraph`.

Illustrare il risultato tipico: con N squadre, la strategia LAZY può produrre 1+N query, mentre le strategie ottimizzate riducono il numero di accessi SQL. Usare esclusivamente i tempi misurati sulla propria macchina.

## 8. Test

Mostrare l'esecuzione:

```powershell
.\mvnw clean test
```

Distinguere:

- test unitari con Mockito;
- test JPA con H2;
- test web e sicurezza con MockMvc.

## 9. Dimostrazione consigliata

1. home e dettaglio torneo;
2. calendario e classifica;
3. login `mario` e inserimento commento;
4. login `admin` e modifica di una partita/risultato;
5. frontend React;
6. un endpoint API nel browser;
7. test verdi;
8. report del benchmark.
