# Strategia di test

## Esecuzione

```bash
./mvnw test
```

I test usano il profilo `test` e un database H2 in memoria. Non leggono né modificano PostgreSQL.

## Livelli coperti

- **Unit test con Mockito**: `TorneoServiceTest`, `PartitaServiceTest`, `CommentoServiceTest`.
- **Integrazione JPA**: `RepositoryIntegrationTest`, che verifica `JOIN FETCH` ed `EntityGraph` sullo stesso grafo dati.
- **Integrazione web e sicurezza**: `WebSecurityIntegrationTest`, con `MockMvc` e Spring Security.

## Casi verificati

- classifica: punti, pareggi, ordinamento, esclusione delle partite `SCHEDULED`;
- partita: creazione, squadre uguali, squadra non iscritta, risultato, goal negativi;
- commenti: normalizzazione, duplicato, proprietà dell'autore, testo vuoto;
- repository: equivalenza tra `JOIN FETCH` ed `EntityGraph`;
- sicurezza/API: accesso pubblico, protezione `/admin`, ruoli USER/ADMIN, errore REST 404.

## Isolamento

`DemoDataInitializer` è disattivato con il profilo `test`. Lo schema H2 viene creato e distrutto per la suite tramite `create-drop`.
