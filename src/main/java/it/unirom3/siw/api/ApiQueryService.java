package it.unirom3.siw.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.api.dto.ClassificaApiDto;
import it.unirom3.siw.api.dto.GiocatoreApiDto;
import it.unirom3.siw.api.dto.PartitaApiDto;
import it.unirom3.siw.api.dto.SquadraDettaglioApiDto;
import it.unirom3.siw.api.dto.SquadraSintesiApiDto;
import it.unirom3.siw.api.dto.TorneoApiDto;
import it.unirom3.siw.api.dto.TorneoDettaglioApiDto;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.EntrataInClassifica;
import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.repository.PartitaRepository;
import it.unirom3.siw.repository.SquadraRepository;
import it.unirom3.siw.repository.TorneoRepository;
import it.unirom3.siw.service.TorneoService;

@Service
public class ApiQueryService {
	private final TorneoRepository torneoRepository;
	private final SquadraRepository squadraRepository;
	private final PartitaRepository partitaRepository;
	private final TorneoService torneoService;

	public ApiQueryService(TorneoRepository torneoRepository, SquadraRepository squadraRepository,
			PartitaRepository partitaRepository, TorneoService torneoService) {
		this.torneoRepository = torneoRepository;
		this.squadraRepository = squadraRepository;
		this.partitaRepository = partitaRepository;
		this.torneoService = torneoService;
	}

	@Transactional(readOnly = true)
	public List<TorneoApiDto> getTornei() {
		return torneoRepository.findAll().stream()
				.sorted(Comparator.comparingInt(Torneo::getAnno).reversed().thenComparing(Torneo::getNome))
				.map(this::toTorneoDto).toList();
	}

	@Transactional(readOnly = true)
	public TorneoDettaglioApiDto getTorneo(Long id) {
		Torneo torneo = torneoRepository.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Torneo non trovato"));
		List<SquadraSintesiApiDto> squadre = torneo.getSquadre().stream().sorted(Comparator.comparing(Squadra::getNome))
				.map(this::toSquadraSintesiDto).toList();
		return new TorneoDettaglioApiDto(torneo.getId(), torneo.getNome(), torneo.getAnno(), torneo.getDescrizione(),
				squadre);
	}

	@Transactional(readOnly = true)
	public List<PartitaApiDto> getCalendario(Long torneoId) {
		if (!torneoRepository.existsById(torneoId)) {
			throw new RisorsaNonTrovataException("Torneo non trovato");
		}
		return partitaRepository.findByTorneoIdOrderByDataOraAsc(torneoId).stream().map(this::toPartitaDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ClassificaApiDto> getClassifica(Long torneoId) {
		List<EntrataInClassifica> classifica = torneoService.getClassifica(torneoId);
		List<ClassificaApiDto> risultato = new ArrayList<>();
		for (int i = 0; i < classifica.size(); i++) {
			EntrataInClassifica e = classifica.get(i);
			risultato.add(new ClassificaApiDto(i + 1, toSquadraSintesiDto(e.getSquadra()), e.getPunti(),
					e.getVittorie(), e.getPareggi(), e.getSconfitte(), e.getGoalFatti(), e.getGoalSubiti(),
					e.getDifferenzaReti()));
		}
		return risultato;
	}

	@Transactional(readOnly = true)
	public SquadraDettaglioApiDto getSquadra(Long id) {
		Squadra squadra = squadraRepository.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Squadra non trovata"));
		List<GiocatoreApiDto> giocatori = squadra.getGiocatori().stream()
				.sorted(Comparator.comparing(Giocatore::getCognome).thenComparing(Giocatore::getNome))
				.map(g -> new GiocatoreApiDto(g.getId(), g.getNome(), g.getCognome(), g.getDataNascita(), g.getRuolo(),
						g.getAltezza()))
				.toList();
		return new SquadraDettaglioApiDto(squadra.getId(), squadra.getNome(), squadra.getAnnoFondazione(),
				squadra.getCitta(), giocatori);
	}

	private TorneoApiDto toTorneoDto(Torneo torneo) {
		return new TorneoApiDto(torneo.getId(), torneo.getNome(), torneo.getAnno(), torneo.getDescrizione());
	}

	private SquadraSintesiApiDto toSquadraSintesiDto(Squadra squadra) {
		return new SquadraSintesiApiDto(squadra.getId(), squadra.getNome(), squadra.getAnnoFondazione(),
				squadra.getCitta());
	}

	private PartitaApiDto toPartitaDto(Partita partita) {
		String arbitro = partita.getArbitro().getNome() + " " + partita.getArbitro().getCognome();
		return new PartitaApiDto(partita.getId(), partita.getDataOra(), partita.getLuogo(), partita.getGoalsHome(),
				partita.getGoalsAway(), partita.getStato(), partita.getTorneo().getId(),
				toSquadraSintesiDto(partita.getHome()), toSquadraSintesiDto(partita.getAway()), arbitro);
	}
}
