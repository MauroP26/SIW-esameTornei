package it.unirom3.siw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.model.*;
import it.unirom3.siw.repository.*;

class PartitaServiceTest {
	PartitaRepository partite = mock(PartitaRepository.class);
	ArbitroRepository arbitri = mock(ArbitroRepository.class);
	TorneoRepository tornei = mock(TorneoRepository.class);
	SquadraRepository squadre = mock(SquadraRepository.class);
	PartitaService service = new PartitaService(partite, arbitri, tornei, squadre);
	Squadra casa = squadra(1L), ospite = squadra(2L);
	Torneo torneo = new Torneo();
	Arbitro arbitro = new Arbitro();

	@BeforeEach
	void setup() {
		torneo.setId(3L);
		torneo.setSquadre(new ArrayList<>(List.of(casa, ospite)));
		arbitro.setId(4L);
		when(tornei.findById(3L)).thenReturn(Optional.of(torneo));
		when(squadre.findById(1L)).thenReturn(Optional.of(casa));
		when(squadre.findById(2L)).thenReturn(Optional.of(ospite));
		when(arbitri.findById(4L)).thenReturn(Optional.of(arbitro));
		when(partite.save(any())).thenAnswer(i -> i.getArgument(0));
	}

	@Test
	void creaPartitaValida() {
		Partita p = service.creaPartita(3L, 1L, 2L, 4L, LocalDateTime.now().plusDays(1), "Campo");
		assertEquals(StatoPartita.SCHEDULED, p.getStato());
		assertSame(casa, p.getHome());
		verify(partite).save(p);
	}

	@Test
	void rifiutaSquadreUguali() {
		assertThrows(RegolaBusinessException.class,
				() -> service.creaPartita(3L, 1L, 1L, 4L, LocalDateTime.now().plusDays(1), "Campo"));
		verify(partite, never()).save(any());
	}

	@Test
	void rifiutaSquadraFuoriTorneo() {
		Squadra esterna = squadra(9L);
		when(squadre.findById(9L)).thenReturn(Optional.of(esterna));
		assertThrows(RegolaBusinessException.class,
				() -> service.creaPartita(3L, 1L, 9L, 4L, LocalDateTime.now().plusDays(1), "Campo"));
	}

	@Test
	void registraRisultato() {
		Partita p = new Partita();
		p.setId(8L);
		p.setDataOra(LocalDateTime.now().minusHours(2));
		p.setStato(StatoPartita.PLAYED);
		when(partite.findById(8L)).thenReturn(Optional.of(p));
		service.registraRisultato(8L, 3, 2);
		assertAll(() -> assertEquals(3, p.getGoalsHome()), () -> assertEquals(2, p.getGoalsAway()),
				() -> assertEquals(StatoPartita.PLAYED, p.getStato()));
	}

	@Test
	void rifiutaGoalNegativi() {
		assertThrows(RegolaBusinessException.class, () -> service.registraRisultato(8L, -1, 0));
	}

	private Squadra squadra(Long id) {
		Squadra s = new Squadra();
		s.setId(id);
		s.setNome("S" + id);
		return s;
	}
}
