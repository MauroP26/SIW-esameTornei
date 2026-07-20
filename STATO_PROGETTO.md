# Stato dopo la prima milestone

## Completato
- Corretto il tipo `RuoloGiocatore` nell'entità `Giocatore`.
- Corretto il repository del calendario delle partite.
- Inizializzate le collezioni delle entità e definite alcune regole di cascade.
- Corretti i service di squadra, giocatore e arbitro.
- Allineati utenti, credenziali e Spring Security.
- Sostituito `import.sql` con dati demo creati tramite `CommandLineRunner` e password BCrypt.
- Rimossa la password PostgreSQL dal codice; ora si usano variabili d'ambiente.
- Uniformate le rotte pubbliche.
- Aggiunte le viste mancanti di elenco e dettaglio partite.
- Aggiunti calendario e classifica nel dettaglio torneo.
- Aggiunto il form di login raggiungibile con GET `/login`.

## Credenziali demo
- ADMIN: `admin` / `admin`
- USER: `mario` / `password`

## Prossima milestone
- Gestione commenti con utente autenticato e modifica consentita solo all'autore.
- Validazione dei form e gestione centralizzata degli errori.
- CRUD amministrativo completo.
- Parte React e analisi sperimentale JPA.

## Verifica
Il comando Maven è stato predisposto, ma in questo ambiente non è stato possibile scaricare la distribuzione Maven richiesta dal wrapper. La compilazione va quindi confermata localmente con `./mvnw test` dopo aver configurato PostgreSQL.

## Milestone 2 - Commenti autenticati
- Visualizzazione pubblica dei commenti nel dettaglio partita.
- Inserimento consentito a utenti `USER` e `ADMIN` autenticati.
- L'autore è ricavato dal `Principal`: il browser non può scegliere l'ID utente.
- Un solo commento per utente e partita, garantito anche da vincolo database.
- Modifica ed eliminazione consentite esclusivamente all'autore.
- Testo obbligatorio, ripulito dagli spazi e limitato a 1000 caratteri.
- Data di creazione e ultima modifica registrate automaticamente.
- Query dedicata con `EntityGraph` per caricare autore e credenziali evitando query aggiuntive nella pagina.

## Prossima milestone consigliata
- Validazione strutturata con form DTO e gestione centralizzata degli errori.
- Successivamente CRUD amministrativo di tornei, squadre e giocatori.

## Milestone 3 - validazione ed errori

Completata:
- DTO dedicati per creazione partita, registrazione risultato e commenti;
- validazione Jakarta con messaggi mostrati nei form Thymeleaf;
- distinzione tra risorse mancanti e violazioni delle regole di business;
- gestione centralizzata tramite `@ControllerAdvice`;
- pagine dedicate per errori 403, 404 e generici;
- gestione dei conflitti di integrità del database;
- vincoli di validazione essenziali anche sul modello persistente.

Prossimo obiettivo: CRUD amministrativo completo per tornei, squadre e giocatori.

## Milestone 4 - CRUD amministrativo

Completato:
- pannello amministrativo riepilogativo;
- creazione, modifica ed eliminazione dei tornei;
- creazione, modifica ed eliminazione controllata delle squadre;
- associazione e rimozione delle squadre dai tornei;
- creazione, modifica, trasferimento ed eliminazione dei giocatori;
- DTO e validazione dedicati per torneo, squadra e giocatore;
- controllo dei duplicati per nome squadra e coppia nome/anno torneo;
- protezione della storia sportiva: una squadra coinvolta in partite non può essere eliminata;
- collegamenti amministrativi integrati nelle pagine di dettaglio pubbliche;
- messaggi flash di conferma dopo le operazioni.

Prossimo obiettivo: completare la gestione amministrativa delle partite (modifica/eliminazione), quindi introdurre API REST e componente React.

## Milestone 5 - gestione partite e arbitri
- creazione e modifica delle partite programmate;
- blocco della modifica strutturale dopo la registrazione del risultato;
- registrazione e correzione del risultato;
- riassegnazione dell'arbitro;
- eliminazione della partita con cancellazione a cascata dei commenti;
- CRUD degli arbitri con codice arbitrale univoco;
- eliminazione arbitro impedita quando associato a partite;
- azioni amministrative integrate nel calendario e nel dettaglio partita.


## Milestone 6 - API REST di lettura
- introdotti DTO JSON separati dalle entità JPA;
- endpoint pubblici per elenco/dettaglio tornei, calendario, classifica e dettaglio squadra;
- mapping eseguito dentro transazioni read-only per gestire correttamente le relazioni LAZY;
- gestione errori REST separata dalle pagine HTML, con risposte 404 e 409 in JSON;
- `/api/**` autorizzato pubblicamente in Spring Security;
- contratto API documentato nel README.

Prossimo obiettivo: frontend React che consumi calendario e classifica, mantenendo Thymeleaf per il resto dell'applicazione.

## Milestone 7 - Frontend React

Completato:
- applicazione React separata e minimale con Vite;
- selezione dinamica del torneo;
- caricamento via REST di dettaglio, calendario e classifica;
- gestione di caricamento ed errori;
- richieste parallele tramite `Promise.all`;
- build configurato dentro le risorse statiche Spring Boot;
- proxy di sviluppo verso il backend;
- guida didattica `GUIDA_REACT.md`.

## Milestone 8 - Analisi prestazionale JPA/Hibernate

Completato:

- caso d'uso sperimentale: caricamento delle rose di un torneo;
- strategia LAZY che rende osservabile N+1;
- query JPQL con JOIN FETCH;
- query con EntityGraph;
- dataset benchmark attivabile solo tramite profilo dedicato;
- warm-up e ripetizioni multiple;
- ordine delle strategie variato in modo riproducibile;
- pulizia del persistence context tra le prove;
- conteggio SQL tramite Hibernate Statistics;
- verifica di equivalenza tramite conteggi e checksum;
- esportazione CSV e Markdown;
- guida didattica e modello di relazione finale.

Da eseguire localmente:

1. avviare con profilo `benchmark`;
2. conservare i file in `benchmark-results`;
3. completare `RELAZIONE_PRESTAZIONI_JPA.md` con ambiente e valori reali;
4. allegare o committare la relazione finale, senza necessariamente versionare tutti i risultati grezzi.


## Milestone 9 — Test automatici

- test unitari dei service con JUnit 5 e Mockito;
- test JPA delle strategie JOIN FETCH ed EntityGraph;
- test MockMvc per API e autorizzazione;
- profilo `test` con H2, separato da PostgreSQL;
- inizializzatore demo disabilitato durante i test;
- guida completa in `TESTING.md`.

## Milestone 10 — Rifinitura e consegna

Completato:

- README finale con avvio backend, test, React, API e benchmark;
- configurazione PostgreSQL basata su variabili d'ambiente;
- configurazione H2 dedicata e coerente per i test;
- allineamento dei test con il comportamento reale dell'applicazione;
- checklist di consegna e pulizia Git;
- guida per la presentazione orale;
- file `.env.example` privo di credenziali reali;
- rimozione delle indicazioni ormai obsolete sulle milestone ancora da completare.
