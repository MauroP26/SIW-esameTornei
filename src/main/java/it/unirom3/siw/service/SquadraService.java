package it.unirom3.siw.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.repository.PartitaRepository;
import it.unirom3.siw.repository.SquadraRepository;
import it.unirom3.siw.repository.TorneoRepository;

@Service
public class SquadraService {
	private final SquadraRepository squadraRepository;
	private final TorneoRepository torneoRepository;
	private final PartitaRepository partitaRepository;

	public SquadraService(SquadraRepository s, TorneoRepository t, PartitaRepository p) {
		squadraRepository = s;
		torneoRepository = t;
		partitaRepository = p;
	}

	@Transactional
	public Squadra creaSquadra(String nome, int anno, String citta) {
		verificaNome(nome, null);
		Squadra s = new Squadra();
		aggiorna(s, nome, anno, citta);
		return squadraRepository.save(s);
	}

	@Transactional
	public Squadra modifica(Long id, String nome, int anno, String citta) {
		Squadra s = getSquadraById(id);
		verificaNome(nome, id);
		aggiorna(s, nome, anno, citta);
		return s;
	}

	private void aggiorna(Squadra s, String n, int a, String c) {
		s.setNome(n.trim());
		s.setAnnoFondazione(a);
		s.setCitta(c.trim());
	}

	private void verificaNome(String n, Long id) {
		squadraRepository.findByNomeIgnoreCase(n.trim()).filter(s -> !s.getId().equals(id)).ifPresent(s -> {
			throw new RegolaBusinessException("Esiste già una squadra con questo nome");
		});
	}

	@Transactional
	public void aggiungiSquadraAlTorneo(Long squadraId, Long torneoId) {
		Torneo t = torneoRepository.findById(torneoId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Torneo non trovato"));
		Squadra s = getSquadraById(squadraId);
		t.aggiungiSquadra(s);
	}

	@Transactional
	public void rimuoviSquadraDalTorneo(Long squadraId, Long torneoId) {
		Torneo t = torneoRepository.findById(torneoId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Torneo non trovato"));
		Squadra s = getSquadraById(squadraId);
		t.getSquadre().remove(s);
		s.getTornei().remove(t);
	}

	@Transactional
	public Squadra salva(Squadra s) {
		return squadraRepository.save(s);
	}

	@Transactional(readOnly = true)
	public List<Squadra> getTutteSquadre() {
		return squadraRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Squadra getSquadraById(Long id) {
		return squadraRepository.findById(id).orElseThrow(() -> new RisorsaNonTrovataException("Squadra non trovata"));
	}

	@Transactional(readOnly = true)
	public Squadra getSquadraByNome(String nome) {
		return squadraRepository.findByNome(nome);
	}

	@Transactional
	public void eliminaSquadra(Long id) {
		Squadra s = getSquadraById(id);
		if (partitaRepository.existsByHomeIdOrAwayId(id, id))
			throw new RegolaBusinessException("La squadra è coinvolta in partite e non può essere eliminata");
		for (Torneo t : new ArrayList<>(s.getTornei()))
			t.getSquadre().remove(s);
		s.getTornei().clear();
		squadraRepository.delete(s);
	}
}
