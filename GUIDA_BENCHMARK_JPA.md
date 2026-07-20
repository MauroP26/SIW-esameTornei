# Guida alla milestone 8: benchmark JPA/Hibernate

## 1. Obiettivo dell'esperimento

Il requisito del docente chiede di confrontare strategie di accesso ai dati e di discutere il problema **N+1 query**.

Il caso d'uso scelto è:

> caricare tutte le squadre partecipanti a un torneo e, per ciascuna squadra, l'intera rosa dei giocatori.

È un caso significativo perché coinvolge tre entità e due relazioni:

```text
Torneo -- molti-a-molti --> Squadra -- uno-a-molti --> Giocatore
```

Il risultato applicativo deve essere identico con tutte le strategie: stesso numero di squadre, stessi giocatori e stesso checksum.

## 2. Perché compare N+1

La relazione `Squadra.giocatori` è `LAZY` per impostazione predefinita di `@OneToMany`.

La strategia di riferimento esegue prima:

```java
squadraRepository.findByTorneiIdOrderByNomeAsc(torneoId)
```

Questa è la query numero 1 e restituisce N squadre. Successivamente il codice percorre le rose:

```java
for (Squadra squadra : squadre) {
    for (Giocatore giocatore : squadra.getGiocatori()) {
        // uso effettivo dei dati
    }
}
```

Alla prima chiamata a `getGiocatori()` di ogni squadra Hibernate inizializza quella collezione con un'altra query. Con 30 squadre ci aspettiamo quindi circa:

```text
1 query per le squadre + 30 query per le rose = 31 query
```

Il nome N+1 deriva proprio da questa forma.

## 3. Strategie confrontate

### 3.1 LAZY / N+1

Repository:

```java
List<Squadra> findByTorneiIdOrderByNomeAsc(Long torneoId);
```

La query iniziale non carica `giocatori`. Le rose vengono recuperate una alla volta durante l'iterazione.

**Vantaggio:** non carica dati non richiesti.

**Svantaggio:** quando sappiamo già di avere bisogno di tutte le rose produce molte query.

### 3.2 JOIN FETCH

Repository:

```java
@Query("""
    select distinct s
    from Squadra s
    join s.tornei t
    left join fetch s.giocatori
    where t.id = :torneoId
    order by s.nome
    """)
List<Squadra> findRoseByTorneoIdJoinFetch(Long torneoId);
```

`JOIN FETCH` comunica a Hibernate che la relazione deve essere materializzata nella stessa interrogazione delle squadre.

`DISTINCT` è necessario perché la join SQL produce una riga per ogni coppia squadra-giocatore; a livello Java vogliamo invece una sola istanza di `Squadra` con la collezione popolata.

**Vantaggio:** strategia esplicita e normalmente una sola query.

**Svantaggio:** la query JPQL conosce direttamente il grafo da caricare; con join multiple il risultato SQL può diventare molto grande.

### 3.3 EntityGraph

Repository:

```java
@EntityGraph(attributePaths = "giocatori")
@Query("""
    select distinct s
    from Squadra s
    join s.tornei t
    where t.id = :torneoId
    order by s.nome
    """)
List<Squadra> findRoseByTorneoIdEntityGraph(Long torneoId);
```

La query stabilisce **quali squadre** cercare. `@EntityGraph` stabilisce **quali associazioni** caricare insieme alle squadre.

**Vantaggio:** separa maggiormente il criterio di ricerca dalla strategia di caricamento.

**Svantaggio:** per grafi molto complessi può essere meno immediato leggere l'SQL atteso.

## 4. Dataset controllato

Il benchmark non usa soltanto i tre team demo, perché con un dataset così piccolo le differenze sarebbero poco evidenti.

Il profilo `benchmark` genera, per impostazione predefinita:

- 30 squadre;
- 15 giocatori per squadra;
- 450 giocatori complessivi;
- un torneo dedicato chiamato `BENCHMARK ACCESSO ROSE`.

I dati vengono creati solo quando il profilo benchmark è attivo. L'avvio normale non viene appesantito.

I parametri sono in `application-benchmark.properties`:

```properties
app.benchmark.squadre=30
app.benchmark.giocatori-per-squadra=15
app.benchmark.warmup=3
app.benchmark.ripetizioni=10
```

## 5. Warm-up

Prima delle misurazioni ogni strategia viene eseguita tre volte senza registrare i risultati.

Il warm-up serve a ridurre l'effetto di:

- caricamento iniziale delle classi;
- compilazione JIT della JVM;
- inizializzazione interna di Hibernate;
- prime allocazioni di memoria.

Non elimina tutte le variazioni, ma rende il confronto più corretto di una singola esecuzione.

## 6. Pulizia del persistence context

Prima di ogni query viene eseguito:

```java
entityManager.clear();
```

Questo svuota la cache di primo livello del persistence context. Senza questa operazione, una strategia eseguita dopo un'altra potrebbe trovare entità già caricate e richiedere meno query soltanto per effetto della cache.

Il benchmark non abilita una cache Hibernate di secondo livello.

## 7. Conteggio delle query

Il profilo abilita Hibernate Statistics:

```properties
spring.jpa.properties.hibernate.generate_statistics=true
```

Prima di ogni misurazione:

```java
statistics.clear();
```

Dopo la chiamata:

```java
long query = statistics.getPrepareStatementCount();
```

Il valore conta i `PreparedStatement` JDBC preparati durante quella prova. Per questo esperimento è una misura pratica del numero di comandi SQL eseguiti.

Il numero di query è più stabile dei tempi assoluti e rappresenta il risultato principale dell'analisi N+1.

## 8. Misurazione dei tempi

Il tempo viene misurato con:

```java
long inizio = System.nanoTime();
// esecuzione completa e consumo delle relazioni
long tempo = System.nanoTime() - inizio;
```

`System.nanoTime()` è adatto alla misura di intervalli trascorsi. Non viene usato `currentTimeMillis()`, che rappresenta l'orario civile e ha una risoluzione inferiore.

Le strategie vengono eseguite in un ordine mescolato ma riproducibile, usando un seme fisso. In questo modo la stessa strategia non beneficia sempre della stessa posizione.

Il report calcola:

- media;
- mediana;
- minimo;
- massimo;
- numero medio di query.

La mediana è utile perché risente meno di singoli picchi anomali.

## 9. Verifica che il confronto sia corretto

Non basta che una strategia sia veloce: deve caricare gli stessi dati.

Ogni esecuzione produce:

- numero di squadre;
- numero di giocatori;
- checksum costruito usando ID e campi testuali letti.

Se una strategia restituisce un risultato diverso, il runner interrompe l'esperimento con un errore. Questo evita di confrontare una query veloce ma incompleta con una query completa.

## 10. Come eseguire il benchmark

Assicurarsi che PostgreSQL sia attivo e che le variabili del database siano configurate.

Da PowerShell, nella directory principale del progetto:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/tournament"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="la_tua_password"
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=benchmark"
```

In STS/Eclipse è possibile aggiungere tra gli argomenti del programma:

```text
--spring.profiles.active=benchmark
```

Il runner parte dopo l'inizializzazione dell'applicazione e stampa una tabella nella console. Al termine l'applicazione web rimane avviata; può essere fermata normalmente.

## 11. File prodotti

Nella root del progetto viene creata la cartella:

```text
benchmark-results/
```

Contiene:

```text
benchmark-jpa-dettaglio.csv
benchmark-jpa-riepilogo.md
```

Il CSV conserva ogni singola misurazione. Il Markdown contiene la tabella riassuntiva e può essere incluso nella relazione finale.

Non bisogna inventare i tempi nella relazione: vanno riportati quelli generati sulla macchina usata per l'esperimento.

## 12. Risultato atteso

Con 30 squadre il comportamento atteso è indicativamente:

| Strategia | Query attese |
|---|---:|
| LAZY_N_PLUS_ONE | circa 31 |
| JOIN_FETCH | circa 1 |
| ENTITY_GRAPH | circa 1 |

I tempi non sono predeterminati. In locale, con database e applicazione sulla stessa macchina, la differenza temporale può essere contenuta; su una rete reale il costo delle numerose andate e ritorno al database diventa generalmente più rilevante.

## 13. Come discutere il risultato all'orale

Una formulazione corretta è:

> Ho confrontato tre strategie sul caricamento delle rose delle squadre di un torneo. Con la strategia LAZY la query delle squadre è seguita da una query per ogni rosa, producendo il problema N+1. JOIN FETCH ed EntityGraph caricano le rose insieme alle squadre e riducono il numero di interrogazioni. Ho svuotato il persistence context tra le prove, effettuato warm-up, ripetuto le misurazioni e verificato con un checksum che tutte le strategie restituissero gli stessi dati. Ho scelto la strategia ottimizzata solo nel caso d'uso che richiede sicuramente le rose, evitando di trasformare globalmente la relazione in EAGER.

## 14. Perché non impostare semplicemente EAGER

Cambiare la relazione in:

```java
@OneToMany(fetch = FetchType.EAGER)
```

non è la soluzione generale. Renderebbe il caricamento dei giocatori automatico in ogni caso d'uso che recupera una squadra, anche quando la rosa non serve.

La scelta consigliata è:

- relazione normalmente `LAZY`;
- query mirata con `JOIN FETCH` o `EntityGraph` nei casi d'uso che richiedono certamente la relazione.

Questo rende esplicito il costo dell'accesso ai dati e limita il caricamento eccessivo.
