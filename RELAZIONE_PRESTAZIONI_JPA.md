# Analisi sperimentale dell'accesso ai dati con JPA/Hibernate

## Caso d'uso

Caricamento delle squadre partecipanti a un torneo e delle rispettive rose. Il caso coinvolge `Torneo`, `Squadra` e `Giocatore` ed è significativo per la pagina di dettaglio di una competizione.

## Strategie

1. **LAZY_N_PLUS_ONE**: query delle squadre seguita dall'inizializzazione separata di ogni collezione `giocatori`.
2. **JOIN_FETCH**: query JPQL con `left join fetch s.giocatori`.
3. **ENTITY_GRAPH**: query delle squadre con `@EntityGraph(attributePaths = "giocatori")`.

## Configurazione sperimentale

Configurazione predefinita:

- 30 squadre;
- 15 giocatori per squadra;
- 450 giocatori;
- 3 warm-up per strategia;
- 10 ripetizioni misurate;
- persistence context svuotato prima di ogni prova;
- Hibernate Statistics per il conteggio dei statement SQL;
- ordine delle strategie variato in modo deterministico.

## Risultati

| Strategia | Query medie | Tempo medio (ms) | Mediana (ms) | Min (ms) | Max (ms) |
|---|---:|---:|---:|---:|---:|
| LAZY_N_PLUS_ONE | 31.0 | 20.201 | 20.139 | 17.656 | 23.144 |
| JOIN_FETCH | 1.0 | 7.688 | 7.614 | 6.919 | 9.174 |
| ENTITY_GRAPH | 1.0 | 10.052 | 10.194 | 8.367 | 11.684 |

## Discussione

La strategia LAZY manifesta N+1 perché, dopo la query delle squadre, Hibernate esegue una query per inizializzare la rosa di ogni squadra. Con N squadre il numero atteso è circa `1 + N`.

JOIN FETCH ed EntityGraph richiedono il caricamento della collezione nella stessa operazione e dovrebbero ridurre il conteggio a circa una query. I tempi assoluti dipendono dall'ambiente, mentre la differenza nel numero di query è più stabile.

Tutte le strategie vengono validate confrontando numero di squadre, numero di giocatori e checksum. Pertanto il confronto non premia una strategia che recupera meno dati.

## Scelta implementativa

La relazione resta `LAZY`. Per il caso d'uso che richiede sicuramente le rose è preferibile una query mirata con `JOIN FETCH` o `EntityGraph`, invece di impostare globalmente `EAGER`. In questo modo altri casi d'uso possono recuperare una squadra senza sostenere sempre il costo della rosa.

Dopo l'esecuzione, indicare quale delle due strategie ottimizzate è stata scelta e motivare la decisione sulla base di leggibilità, riuso e risultati misurati.
