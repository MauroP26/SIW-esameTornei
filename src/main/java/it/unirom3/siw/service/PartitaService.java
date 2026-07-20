package it.unirom3.siw.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.Arbitro;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.StatoPartita;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.repository.ArbitroRepository;
import it.unirom3.siw.repository.PartitaRepository;
import it.unirom3.siw.repository.SquadraRepository;
import it.unirom3.siw.repository.TorneoRepository;

@Service
public class PartitaService {

	private PartitaRepository partitaRepo;
	private ArbitroRepository arbitroRepo;
	private TorneoRepository torneoRepository;
	private SquadraRepository squadraRepository;

	public PartitaService(PartitaRepository p, ArbitroRepository a, TorneoRepository t, SquadraRepository s) {
		this.partitaRepo = p;
		this.arbitroRepo = a;
		this.torneoRepository = t;
		this.squadraRepository = s;
	}

	@Transactional
	public Partita salva(Partita p) {
		return partitaRepo.save(p);
	}

	@Transactional
	public Partita creaPartita(Long torneoId, Long squadraCasaId, Long squadraOspiteId, Long arbitroId,
			LocalDateTime dataOra, String luogo) {

		if (dataOra == null || dataOra.isBefore(LocalDateTime.now()))
			throw new RegolaBusinessException("La partita non può essere programmata nel passato");

		Torneo torneo = torneoRepository.findById(torneoId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Torneo non trovato"));

		Squadra casa = squadraRepository.findById(squadraCasaId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Squadra non trovata"));

		Squadra ospite = squadraRepository.findById(squadraOspiteId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Squadra non trovata"));

		if (casa.getId().equals(ospite.getId())) {
			throw new RegolaBusinessException("Le due squadre devono essere diverse");
		}

		if (!torneo.getSquadre().contains(casa) || !torneo.getSquadre().contains(ospite)) {
			throw new RegolaBusinessException("Entrambe le squadre devono partecipare al torneo");
		}

		Arbitro arbitro = arbitroRepo.findById(arbitroId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Arbitro non trovato"));

		Partita partita = new Partita();

		partita.setTorneo(torneo);
		partita.setHome(casa);
		partita.setAway(ospite);
		partita.setArbitro(arbitro);
		partita.setDataOra(dataOra);
		partita.setLuogo(luogo);
		partita.setGoalsHome(0);
		partita.setGoalsAway(0);
		partita.setStato(StatoPartita.SCHEDULED);

		return partitaRepo.save(partita);

	}

	@Transactional
	public Partita modificaPartita(Long id, Long torneoId, Long squadraCasaId, Long squadraOspiteId, Long arbitroId,
			LocalDateTime dataOra, String luogo) {
		Partita partita = partitaRepo.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));
		if (partita.getStato() == StatoPartita.PLAYED)
			throw new RegolaBusinessException("Una partita già disputata non può essere modificata");
		if (dataOra == null || dataOra.isBefore(LocalDateTime.now()))
			throw new RegolaBusinessException("La partita non può essere programmata nel passato");
		Torneo torneo = torneoRepository.findById(torneoId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Torneo non trovato"));
		Squadra casa = squadraRepository.findById(squadraCasaId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Squadra di casa non trovata"));
		Squadra ospite = squadraRepository.findById(squadraOspiteId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Squadra ospite non trovata"));
		if (casa.getId().equals(ospite.getId()))
			throw new RegolaBusinessException("Le due squadre devono essere diverse");
		if (!torneo.getSquadre().contains(casa) || !torneo.getSquadre().contains(ospite))
			throw new RegolaBusinessException("Entrambe le squadre devono partecipare al torneo");
		Arbitro arbitro = arbitroRepo.findById(arbitroId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Arbitro non trovato"));
		partita.setTorneo(torneo);
		partita.setHome(casa);
		partita.setAway(ospite);
		partita.setArbitro(arbitro);
		partita.setDataOra(dataOra);
		partita.setLuogo(luogo.trim());
		return partitaRepo.save(partita);
	}

	@Transactional(readOnly = true)
	public List<Partita> getTuttePartite() {
		return partitaRepo.findAllByOrderByDataOraAsc();
	}

	@Transactional(readOnly = true)
	public Optional<Partita> getPartitaById(Long id) {
		return partitaRepo.findById(id);
	}

	@Transactional
	public void eliminaPartita(Long id) {
		Partita partita = partitaRepo.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));
		partitaRepo.delete(partita);
	}

	@Transactional(readOnly = true)
	public List<Partita> calendario(Long torneoId) {
		return partitaRepo.findByTorneoIdOrderByDataOraAsc(torneoId);
	}

	@Transactional
	public void assegnaArbitro(Long partitaId, Long arbitroId) {

		Partita partita = partitaRepo.findById(partitaId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));

		Arbitro arbitro = arbitroRepo.findById(arbitroId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Arbitro non trovato"));

		partita.setArbitro(arbitro);
		partitaRepo.save(partita);

	}

	@Transactional
	public void registraRisultato(Long partitaId, Integer goalCasa, Integer goalOspiti) {

		if (goalCasa == null || goalOspiti == null || goalCasa < 0 || goalOspiti < 0) {
			throw new RegolaBusinessException("I gol devono essere numeri non negativi");
		}

		Partita partita = partitaRepo.findById(partitaId)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));

		if (partita.getDataOra() == null || partita.getDataOra().isAfter(LocalDateTime.now())) {

			throw new RegolaBusinessException(
					"Non è possibile registrare il risultato prima dell'inizio della partita");
		}

		partita.setGoalsHome(goalCasa);
		partita.setGoalsAway(goalOspiti);
		partita.setStato(StatoPartita.PLAYED);
		partitaRepo.save(partita);

	}
}
