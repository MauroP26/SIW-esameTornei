async function getJson(url) {
  const response = await fetch(url, {
    headers: { Accept: 'application/json' }
  });

  if (!response.ok) {
    let message = `Richiesta non riuscita (${response.status})`;
    try {
      const error = await response.json();
      message = error.messaggio ?? message;
    } catch {
      // La risposta non contiene JSON: manteniamo il messaggio generico.
    }
    throw new Error(message);
  }

  return response.json();
}

export const torneoApi = {
  getTornei: () => getJson('/api/tornei'),
  getDettaglio: (torneoId) => getJson(`/api/tornei/${torneoId}`),
  getCalendario: (torneoId) => getJson(`/api/tornei/${torneoId}/partite`),
  getClassifica: (torneoId) => getJson(`/api/tornei/${torneoId}/classifica`)
};
