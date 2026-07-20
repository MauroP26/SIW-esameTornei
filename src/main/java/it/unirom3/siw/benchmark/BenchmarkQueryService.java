package it.unirom3.siw.benchmark;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.repository.SquadraRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class BenchmarkQueryService {

	private final SquadraRepository squadraRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public BenchmarkQueryService(SquadraRepository squadraRepository) {
		this.squadraRepository = squadraRepository;
	}

	@Transactional(readOnly = true)
	public CaricamentoRoseResult caricaRoseLazy(Long torneoId) {
		preparaPersistenceContext();
		List<Squadra> squadre = squadraRepository.findByTorneiIdOrderByNomeAsc(torneoId);
		return consumaTuttiIDati(squadre);
	}

	@Transactional(readOnly = true)
	public CaricamentoRoseResult caricaRoseJoinFetch(Long torneoId) {
		preparaPersistenceContext();
		List<Squadra> squadre = squadraRepository.findRoseByTorneoIdJoinFetch(torneoId);
		return consumaTuttiIDati(squadre);
	}

	@Transactional(readOnly = true)
	public CaricamentoRoseResult caricaRoseEntityGraph(Long torneoId) {
		preparaPersistenceContext();
		List<Squadra> squadre = squadraRepository.findRoseByTorneoIdEntityGraph(torneoId);
		return consumaTuttiIDati(squadre);
	}

	private void preparaPersistenceContext() {
		/*
		 * Ogni prova deve partire senza entità già presenti nella cache di primo
		 * livello. In caso contrario una strategia eseguita dopo un'altra potrebbe
		 * riutilizzare oggetti già caricati e apparire artificialmente più veloce.
		 */
		entityManager.clear();
	}

	private CaricamentoRoseResult consumaTuttiIDati(List<Squadra> squadre) {
		int giocatori = 0;
		long checksum = 0;

		for (Squadra squadra : squadre) {
			checksum += squadra.getId();
			checksum += squadra.getNome().length();

			/*
			 * Questo accesso è intenzionale: nella strategia LAZY inizializza una
			 * collezione per ciascuna squadra e rende osservabile il problema N+1.
			 */
			for (Giocatore giocatore : squadra.getGiocatori()) {
				giocatori++;
				checksum += giocatore.getId();
				checksum += giocatore.getNome().length();
				checksum += giocatore.getCognome().length();
			}
		}

		return new CaricamentoRoseResult(squadre.size(), giocatori, checksum);
	}
}
