package it.unirom3.siw.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.RuoloGiocatore;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.repository.GiocatoreRepository;
import it.unirom3.siw.repository.SquadraRepository;

@Service
public class GiocatoreService {
	private final SquadraRepository squadraRepository;
	private final GiocatoreRepository giocatoreRepository;

	public GiocatoreService(GiocatoreRepository g, SquadraRepository s) {
		giocatoreRepository = g;
		squadraRepository = s;
	}

	@Transactional
	public Giocatore aggiungiGiocatore(String n, String c, LocalDate d, RuoloGiocatore r, double a, Long squadraId) {
		Giocatore g = new Giocatore();
		aggiorna(g, n, c, d, r, a, getSquadra(squadraId));
		return giocatoreRepository.save(g);
	}

	@Transactional
	public Giocatore modifica(Long id, String n, String c, LocalDate d, RuoloGiocatore r, double a, Long squadraId) {
		Giocatore g = getGiocatoreById(id);
		Squadra vecchia = g.getSquadra(), nuova = getSquadra(squadraId);
		if (!vecchia.getId().equals(nuova.getId())) {
			vecchia.getGiocatori().remove(g);
			nuova.getGiocatori().add(g);
		}
		aggiorna(g, n, c, d, r, a, nuova);
		return g;
	}

	private void aggiorna(Giocatore g, String n, String c, LocalDate d, RuoloGiocatore r, double a, Squadra s) {
		g.setNome(n.trim());
		g.setCognome(c.trim());
		g.setDataNascita(d);
		g.setRuolo(r);
		g.setAltezza(a);
		g.setSquadra(s);
		if (!s.getGiocatori().contains(g))
			s.getGiocatori().add(g);
	}

	private Squadra getSquadra(Long id) {
		return squadraRepository.findById(id).orElseThrow(() -> new RisorsaNonTrovataException("Squadra non trovata"));
	}

	@Transactional(readOnly = true)
	public List<Giocatore> getTuttiGiocatori() {
		return giocatoreRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Giocatore> getGiocatoriBySquadra(Squadra s) {
		return giocatoreRepository.findBySquadra(s);
	}

	@Transactional(readOnly = true)
	public Giocatore getGiocatoreById(Long id) {
		return giocatoreRepository.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Giocatore non trovato"));
	}

	@Transactional
	public void eliminaGiocatore(Long id) {
		Giocatore g = getGiocatoreById(id);
		g.getSquadra().getGiocatori().remove(g);
		giocatoreRepository.delete(g);
	}
}
