package it.unirom3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.Commento;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.model.Utente;
import it.unirom3.siw.repository.CommentoRepository;
import it.unirom3.siw.repository.PartitaRepository;
import it.unirom3.siw.repository.UtenteRepository;

@Service
public class CommentoService {
	private final CommentoRepository commentoRepository;
	private final PartitaRepository partitaRepository;
	private final UtenteRepository utenteRepository;

	public CommentoService(CommentoRepository commentoRepository, PartitaRepository partitaRepository,
			UtenteRepository utenteRepository) {
		this.commentoRepository = commentoRepository;
		this.partitaRepository = partitaRepository;
		this.utenteRepository = utenteRepository;
	}

	@Transactional(readOnly = true)
	public List<Commento> getCommentiPartita(Long partitaId) {
		return commentoRepository.findByPartitaIdOrderByCreatoIlAsc(partitaId);
	}

	@Transactional(readOnly = true)
	public Optional<Commento> getCommentoUtente(Long partitaId, String username) {
		return commentoRepository.findByPartitaIdAndAutoreCredUsername(partitaId, username);
	}

	@Transactional
	public Commento aggiungiCommento(Long partitaId, String username, String testo) {
		String testoNormalizzato = validaTesto(testo);
		if (commentoRepository.findByPartitaIdAndAutoreCredUsername(partitaId, username).isPresent()) {
			throw new RegolaBusinessException("Hai già inserito un commento per questa partita");
		}

		Partita partita = partitaRepository.findById(partitaId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));
		Utente autore = utenteRepository.findByCredUsername(username)
				.orElseThrow(() -> new RisorsaNonTrovataException("Utente autenticato non trovato"));

		Commento commento = new Commento();
		commento.setTesto(testoNormalizzato);
		commento.setPartita(partita);
		commento.setAutore(autore);
		partita.getCommenti().add(commento);
		autore.getCommenti().add(commento);
		return commentoRepository.save(commento);
	}

	@Transactional
	public Commento modificaCommento(Long commentoId, String username, String testo) {
		Commento commento = commentoRepository.findById(commentoId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Commento non trovato"));
		verificaAutore(commento, username);
		commento.setTesto(validaTesto(testo));
		return commento;
	}

	@Transactional
	public Long eliminaCommento(Long commentoId, String username) {
		Commento commento = commentoRepository.findById(commentoId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Commento non trovato"));
		verificaAutore(commento, username);
		Long partitaId = commento.getPartita().getId();
		commentoRepository.delete(commento);
		return partitaId;
	}

	@Transactional(readOnly = true)
	public Long getPartitaIdDelCommento(Long commentoId) {
		return commentoRepository.findById(commentoId).map(commento -> commento.getPartita().getId())
				.orElseThrow(() -> new RisorsaNonTrovataException("Commento non trovato"));
	}

	private void verificaAutore(Commento commento, String username) {
		if (!commento.getAutore().getCred().getUsername().equals(username)) {
			throw new AccessDeniedException("Puoi modificare o eliminare soltanto i tuoi commenti");
		}
	}

	private String validaTesto(String testo) {
		if (testo == null || testo.isBlank()) {
			throw new RegolaBusinessException("Il commento non può essere vuoto");
		}
		String normalizzato = testo.trim();
		if (normalizzato.length() > 1000) {
			throw new RegolaBusinessException("Il commento non può superare 1000 caratteri");
		}
		return normalizzato;
	}
}
