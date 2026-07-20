# Checklist finale di consegna

## Codice e configurazione

- [ ] `./mvnw clean test` termina con `BUILD SUCCESS`.
- [ ] PostgreSQL è raggiungibile con le variabili `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- [ ] Il progetto parte con `./mvnw spring-boot:run`.
- [ ] Non sono presenti password personali nel repository.
- [ ] Non sono presenti `import.sql`, log locali, `target/`, `node_modules/` o risultati grezzi non necessari.
- [ ] Il file `.gitignore` esclude artefatti di compilazione e file locali.

## Verifica funzionale

- [ ] Le pagine pubbliche sono accessibili senza autenticazione.
- [ ] L'utente `mario` può inserire e gestire soltanto i propri commenti.
- [ ] L'utente `mario` non può accedere a `/admin`.
- [ ] L'utente `admin` può gestire tutte le entità previste.
- [ ] Calendario e classifica mostrano dati coerenti con i risultati.
- [ ] Le API REST restituiscono JSON e `404` per risorse inesistenti.
- [ ] Il frontend React carica tornei, calendario e classifica.

## Benchmark e relazione

- [ ] Il benchmark è stato eseguito sulla macchina usata per la relazione.
- [ ] Sono stati annotati hardware, Java, PostgreSQL e numero di ripetizioni.
- [ ] La relazione contiene tempi reali e numero di query.
- [ ] È spiegato il problema N+1.
- [ ] È motivata la scelta di mantenere le associazioni `LAZY` e ottimizzare i casi d'uso mirati.

## GitHub

- [ ] Il repository contiene un README aggiornato.
- [ ] Il primo commit non include credenziali o database locali.
- [ ] La cronologia dei commit è comprensibile.
- [ ] Il branch consegnato compila e corrisponde alla versione dimostrata.
- [ ] Il link al repository è verificato in modalità anonima/incognito.
