package it.unirom3.siw.benchmark;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.RuoloGiocatore;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.repository.SquadraRepository;
import it.unirom3.siw.repository.TorneoRepository;
import jakarta.persistence.EntityManager;

@Service
public class BenchmarkDataService {

	public static final String NOME_TORNEO = "BENCHMARK ACCESSO ROSE";
	public static final int ANNO_TORNEO = 2099;

	private final TorneoRepository torneoRepository;
	private final SquadraRepository squadraRepository;
	private final EntityManager entityManager;

	public BenchmarkDataService(TorneoRepository torneoRepository, SquadraRepository squadraRepository,
			EntityManager entityManager) {
		this.torneoRepository = torneoRepository;
		this.squadraRepository = squadraRepository;
		this.entityManager = entityManager;
	}

	@Transactional
	public Long preparaDataset(int numeroSquadre, int giocatoriPerSquadra) {
		Torneo esistente = torneoRepository.findByNomeIgnoreCaseAndAnno(NOME_TORNEO, ANNO_TORNEO).orElse(null);

		if (esistente != null && esistente.getSquadre().size() == numeroSquadre
				&& esistente.getSquadre().stream().allMatch(s -> s.getGiocatori().size() == giocatoriPerSquadra)) {
			return esistente.getId();
		}

		if (esistente != null) {
			List<Squadra> vecchieSquadre = new ArrayList<>(esistente.getSquadre());
			for (Squadra squadra : vecchieSquadre) {
				squadra.getTornei().remove(esistente);
			}
			esistente.getSquadre().clear();
			torneoRepository.delete(esistente);
			torneoRepository.flush();
			squadraRepository.deleteAll(vecchieSquadre);
			squadraRepository.flush();
		}

		List<Squadra> squadre = new ArrayList<>();
		for (int i = 1; i <= numeroSquadre; i++) {
			Squadra squadra = new Squadra();
			squadra.setNome("Benchmark Squadra %03d".formatted(i));
			squadra.setAnnoFondazione(2000 + (i % 20));
			squadra.setCitta("Citta %02d".formatted(i % 10));

			for (int j = 1; j <= giocatoriPerSquadra; j++) {
				Giocatore giocatore = new Giocatore();
				giocatore.setNome("Nome%03d_%02d".formatted(i, j));
				giocatore.setCognome("Cognome%03d_%02d".formatted(i, j));
				giocatore.setDataNascita(LocalDate.of(1985 + (j % 18), (j % 12) + 1, (j % 27) + 1));
				giocatore.setRuolo(RuoloGiocatore.values()[j % RuoloGiocatore.values().length]);
				giocatore.setAltezza(1.65 + ((j % 25) / 100.0));
				squadra.aggiungiGiocatore(giocatore);
			}
			squadre.add(squadra);
		}

		squadraRepository.saveAll(squadre);

		Torneo torneo = new Torneo();
		torneo.setNome(NOME_TORNEO);
		torneo.setAnno(ANNO_TORNEO);
		torneo.setDescrizione("Dataset generato esclusivamente per il benchmark JPA");
		squadre.forEach(torneo::aggiungiSquadra);
		torneoRepository.saveAndFlush(torneo);
		entityManager.clear();
		return torneo.getId();
	}
}
