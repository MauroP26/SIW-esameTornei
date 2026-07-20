package it.unirom3.siw.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.EntrataInClassifica;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.StatoPartita;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.repository.TorneoRepository;

@Service
public class TorneoService {
	private final TorneoRepository torneoRepository;

	public TorneoService(TorneoRepository torneoRepository) {
		this.torneoRepository = torneoRepository;
	}

	@Transactional
	public Torneo crea(String nome, int anno, String descrizione) {
		verificaDuplicato(nome, anno, null);
		Torneo t = new Torneo();
		aggiornaCampi(t, nome, anno, descrizione);
		return torneoRepository.save(t);
	}

	@Transactional
	public Torneo modifica(Long id, String nome, int anno, String descrizione) {
		Torneo t = getTorneoById(id);
		verificaDuplicato(nome, anno, id);
		aggiornaCampi(t, nome, anno, descrizione);
		return t;
	}

	private void aggiornaCampi(Torneo t, String nome, int anno, String descrizione) {
		t.setNome(nome.trim());
		t.setAnno(anno);
		t.setDescrizione(descrizione == null ? "" : descrizione.trim());
	}

	private void verificaDuplicato(String nome, int anno, Long id) {
		torneoRepository.findByNomeIgnoreCaseAndAnno(nome.trim(), anno).filter(t -> !t.getId().equals(id))
				.ifPresent(t -> {
					throw new RegolaBusinessException("Esiste già un torneo con lo stesso nome e anno");
				});
	}

	@Transactional
	public Torneo salva(Torneo torneo) {
		return torneoRepository.save(torneo);
	}

	@Transactional(readOnly = true)
	public List<Torneo> getTuttiTornei() {
		return torneoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Torneo getTorneoById(Long id) {
		return torneoRepository.findById(id).orElseThrow(() -> new RisorsaNonTrovataException("Torneo non trovato"));
	}

	@Transactional(readOnly = true)
	public Optional<Torneo> getTorneoByNome(String nome) {
		return torneoRepository.findByNome(nome);
	}

	private void inizializzaSquadra(Map<Squadra, EntrataInClassifica> c, Squadra s) {
		c.computeIfAbsent(s, EntrataInClassifica::new);
	}

	private void aggiornaStatistiche(Partita p, Map<Squadra, EntrataInClassifica> c) {
		EntrataInClassifica casa = c.get(p.getHome()), trasferta = c.get(p.getAway());
		casa.setGoalFatti(casa.getGoalFatti() + p.getGoalsHome());
		casa.setGoalSubiti(casa.getGoalSubiti() + p.getGoalsAway());
		trasferta.setGoalFatti(trasferta.getGoalFatti() + p.getGoalsAway());
		trasferta.setGoalSubiti(trasferta.getGoalSubiti() + p.getGoalsHome());
		if (p.getGoalsHome() > p.getGoalsAway()) {
			casa.setPunti(casa.getPunti() + 3);
			casa.setVittorie(casa.getVittorie() + 1);
			trasferta.setSconfitte(trasferta.getSconfitte() + 1);
		} else if (p.getGoalsHome() == p.getGoalsAway()) {
			casa.setPunti(casa.getPunti() + 1);
			trasferta.setPunti(trasferta.getPunti() + 1);
			casa.setPareggi(casa.getPareggi() + 1);
			trasferta.setPareggi(trasferta.getPareggi() + 1);
		} else {
			trasferta.setPunti(trasferta.getPunti() + 3);
			trasferta.setVittorie(trasferta.getVittorie() + 1);
			casa.setSconfitte(casa.getSconfitte() + 1);
		}
	}

	@Transactional(readOnly = true)
	public List<EntrataInClassifica> getClassifica(Long id) {
		Map<Squadra, EntrataInClassifica> c = new HashMap<>();
		Torneo t = getTorneoById(id);
		t.getSquadre().forEach(s -> inizializzaSquadra(c, s));
		for (Partita p : t.getPartite())
			if (p.getStato() == StatoPartita.PLAYED) {
				inizializzaSquadra(c, p.getHome());
				inizializzaSquadra(c, p.getAway());
				aggiornaStatistiche(p, c);
			}
		List<EntrataInClassifica> result = new ArrayList<>(c.values());
		result.sort(Comparator.comparingInt(EntrataInClassifica::getPunti)
				.thenComparingInt(EntrataInClassifica::getDifferenzaReti).reversed());
		return result;
	}

	@Transactional
	public void eliminaTorneo(Long id) {
		Torneo t = getTorneoById(id);
		for (Squadra s : new ArrayList<>(t.getSquadre()))
			s.getTornei().remove(t);
		t.getSquadre().clear();
		torneoRepository.delete(t);
	}
}
