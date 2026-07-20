# Guida alla parte React

## 1. Che cosa è stato realizzato

La parte React è volutamente circoscritta a un caso d'uso pubblico e significativo:

1. caricare l'elenco dei tornei;
2. selezionare un torneo;
3. caricarne in parallelo dettaglio, calendario e classifica;
4. aggiornare l'interfaccia senza ricaricare l'intera pagina.

Login e amministrazione continuano a usare Spring MVC e Thymeleaf. React non sostituisce tutto il frontend: soddisfa il requisito del progetto realizzandone una parte autonoma.

## 2. Struttura

```text
frontend-react/
├── index.html
├── package.json
├── vite.config.js
└── src/
    ├── main.jsx       punto di ingresso
    ├── App.jsx        stato e componenti dell'interfaccia
    ├── api.js          chiamate HTTP alle API Spring
    └── styles.css      stile della pagina
```

## 3. Flusso dei dati

```text
Browser React
   │ fetch('/api/tornei/...')
   ▼
TorneoRestController
   ▼
ApiQueryService
   ▼
Service / Repository JPA
   ▼
DTO JSON
   ▼
state React → nuovo rendering
```

React non interroga PostgreSQL e non conosce Hibernate. Comunica esclusivamente via HTTP con gli endpoint REST.

## 4. Concetti React usati

### Componenti

`App`, `Classifica` e `Calendario` sono funzioni JavaScript che restituiscono JSX. Ogni componente descrive una parte dell'interfaccia.

### Stato con `useState`

Esempio:

```jsx
const [torneoId, setTorneoId] = useState('');
```

- `torneoId` contiene il valore attuale;
- `setTorneoId` lo modifica;
- una modifica dello stato provoca un nuovo rendering.

### Effetti con `useEffect`

Il primo `useEffect` viene eseguito al montaggio del componente e carica l'elenco dei tornei.

Il secondo dipende da `torneoId`: viene rieseguito ogni volta che l'utente sceglie un altro torneo.

```jsx
useEffect(() => {
  // caricamento dei dati del torneo
}, [torneoId]);
```

### Richieste parallele

```jsx
const [dettaglio, calendario, classifica] = await Promise.all([...]);
```

Le tre chiamate non aspettano una l'altra. Questo riduce il tempo complessivo rispetto a tre richieste sequenziali.

### Rendering condizionale

```jsx
{loading && <p>Caricamento dati…</p>}
{error && <p>{error}</p>}
```

Un elemento viene mostrato solo quando la condizione è vera.

### Liste e `key`

```jsx
rows.map(row => <tr key={row.squadra.id}>...</tr>)
```

`map` trasforma i dati in elementi JSX. `key` permette a React di riconoscere stabilmente ogni riga durante gli aggiornamenti.

## 5. API usate

- `GET /api/tornei`
- `GET /api/tornei/{id}`
- `GET /api/tornei/{id}/partite`
- `GET /api/tornei/{id}/classifica`

La logica HTTP è isolata in `src/api.js`. Se cambia un URL, non è necessario modificare tutti i componenti.

## 6. Avvio in sviluppo

Avviare prima Spring Boot sulla porta 8080.

In un secondo terminale:

```bash
cd frontend-react
npm install
npm run dev
```

Aprire `http://localhost:5173`.

Vite riceve le richieste `/api` dal browser e le inoltra a `http://localhost:8080`, secondo la configurazione `server.proxy` in `vite.config.js`.

## 7. Build integrato in Spring Boot

```bash
cd frontend-react
npm install
npm run build
```

Il risultato viene scritto in:

```text
src/main/resources/static/react-app/
```

Dopo il build, avviando Spring Boot la pagina è disponibile a:

```text
http://localhost:8080/react-app/index.html
```

Il frontend e le API hanno allora la stessa origine, quindi non serve CORS.

## 8. Perché non restituiamo entità JPA

Le API restituiscono DTO. Le entità potrebbero contenere relazioni circolari e proprietà che il frontend non dovrebbe conoscere. I DTO definiscono un contratto JSON esplicito e più stabile.

## 9. Possibili domande d'esame

**Perché React?** Per rendere dinamica una parte dell'interfaccia: cambiando torneo, calendario e classifica vengono aggiornati senza un nuovo caricamento completo della pagina.

**Perché `useEffect`?** Per sincronizzare il componente con una risorsa esterna, in questo caso le API REST.

**Perché uno stato separato per loading ed error?** Per rappresentare esplicitamente le diverse condizioni della richiesta asincrona.

**Perché Vite?** Fornisce server di sviluppo, trasformazione JSX e build ottimizzato con configurazione minima.

**Thymeleaf e React possono convivere?** Sì. Thymeleaf produce pagine lato server; React gestisce una parte separata lato client che comunica con Spring tramite JSON.
