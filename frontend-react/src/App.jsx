import { useEffect, useState } from 'react';
import { torneoApi } from './api.js';

const EMPTY_DATA = { dettaglio: null, calendario: [], classifica: [] };

function App() {
  const [tornei, setTornei] = useState([]);
  const [torneoId, setTorneoId] = useState('');
  const [data, setData] = useState(EMPTY_DATA);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function caricaTornei() {
      try {
        setLoading(true);
        const elenco = await torneoApi.getTornei();
        setTornei(elenco);
        if (elenco.length > 0) {
          setTorneoId(String(elenco[0].id));
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    caricaTornei();
  }, []);

  useEffect(() => {
    if (!torneoId) {
      setData(EMPTY_DATA);
      return;
    }

    let richiestaAttiva = true;

    async function caricaTorneo() {
      try {
        setLoading(true);
        setError('');
        const [dettaglio, calendario, classifica] = await Promise.all([
          torneoApi.getDettaglio(torneoId),
          torneoApi.getCalendario(torneoId),
          torneoApi.getClassifica(torneoId)
        ]);

        if (richiestaAttiva) {
          setData({ dettaglio, calendario, classifica });
        }
      } catch (err) {
        if (richiestaAttiva) setError(err.message);
      } finally {
        if (richiestaAttiva) setLoading(false);
      }
    }

    caricaTorneo();
    return () => {
      richiestaAttiva = false;
    };
  }, [torneoId]);

  return (
    <main className="page-shell">
      <header className="hero">
        <div>
          <p className="eyebrow">Frontend React</p>
          <h1>Calendario e classifica</h1>
          <p>Seleziona un torneo. I dati vengono letti dalle API REST di Spring Boot.</p>
        </div>
        <a className="secondary-link" href="/">Torna al sito Thymeleaf</a>
      </header>

      <section className="selector-card" aria-labelledby="selezione-torneo">
        <label id="selezione-torneo" htmlFor="torneo">Torneo</label>
        <select
          id="torneo"
          value={torneoId}
          onChange={(event) => setTorneoId(event.target.value)}
          disabled={loading && tornei.length === 0}
        >
          {tornei.length === 0 && <option value="">Nessun torneo disponibile</option>}
          {tornei.map((torneo) => (
            <option key={torneo.id} value={torneo.id}>
              {torneo.nome} ({torneo.anno})
            </option>
          ))}
        </select>
      </section>

      {error && <p className="alert" role="alert">{error}</p>}
      {loading && <p className="status" aria-live="polite">Caricamento dati…</p>}

      {!loading && data.dettaglio && (
        <>
          <section className="tournament-summary">
            <h2>{data.dettaglio.nome} <span>{data.dettaglio.anno}</span></h2>
            <p>{data.dettaglio.descrizione || 'Nessuna descrizione disponibile.'}</p>
            <p><strong>Squadre partecipanti:</strong> {data.dettaglio.squadre.length}</p>
          </section>

          <div className="content-grid">
            <Classifica rows={data.classifica} />
            <Calendario partite={data.calendario} />
          </div>
        </>
      )}
    </main>
  );
}

function Classifica({ rows }) {
  return (
    <section className="panel">
      <h2>Classifica</h2>
      {rows.length === 0 ? (
        <p>Nessuna squadra in classifica.</p>
      ) : (
        <div className="table-scroll">
          <table>
            <thead>
              <tr><th>#</th><th>Squadra</th><th>Pt</th><th>G</th><th>V</th><th>N</th><th>P</th><th>DR</th></tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.squadra.id}>
                  <td>{row.posizione}</td>
                  <td>{row.squadra.nome}</td>
                  <td><strong>{row.punti}</strong></td>
                  <td>{row.vittorie + row.pareggi + row.sconfitte}</td>
                  <td>{row.vittorie}</td>
                  <td>{row.pareggi}</td>
                  <td>{row.sconfitte}</td>
                  <td>{formatDifferenzaReti(row.differenzaReti)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

function Calendario({ partite }) {
  return (
    <section className="panel">
      <h2>Calendario</h2>
      {partite.length === 0 ? (
        <p>Nessuna partita registrata.</p>
      ) : (
        <div className="matches">
          {partite.map((partita) => (
            <article className="match-card" key={partita.id}>
              <div className="match-meta">
                <time dateTime={partita.dataOra}>{formatData(partita.dataOra)}</time>
                <span className={`badge badge-${partita.stato.toLowerCase()}`}>{partita.stato}</span>
              </div>
              <div className="score-line">
                <span>{partita.squadraCasa.nome}</span>
                <strong>{partita.stato === 'PLAYED' ? `${partita.goalsHome} – ${partita.goalsAway}` : 'vs'}</strong>
                <span>{partita.squadraOspite.nome}</span>
              </div>
              <p>{partita.luogo}{partita.arbitro ? ` · Arbitro: ${partita.arbitro}` : ''}</p>
              <a href={`/partite/${partita.id}`}>Dettaglio e commenti</a>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

function formatData(value) {
  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}

function formatDifferenzaReti(value) {
  return value > 0 ? `+${value}` : value;
}

export default App;
