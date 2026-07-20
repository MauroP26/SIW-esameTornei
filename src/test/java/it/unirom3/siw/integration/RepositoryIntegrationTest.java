package it.unirom3.siw.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import it.unirom3.siw.model.*;
import it.unirom3.siw.repository.*;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {
	@Autowired
	SquadraRepository squadre;
	@Autowired
	TorneoRepository tornei;

	@Test
	void joinFetchEdEntityGraphCaricanoLoStessoGrafo() {
		Squadra s = new Squadra();
		s.setNome("Test");
		s.setCitta("Roma");
		s.setAnnoFondazione(2020);
		Giocatore g = new Giocatore();
		g.setNome("Mario");
		g.setCognome("Rossi");
		g.setDataNascita(LocalDate.of(2000, 1, 1));
		g.setRuolo(RuoloGiocatore.ATTACCANTE);
		g.setAltezza(1.8);
		s.aggiungiGiocatore(g);
		squadre.save(s);
		Torneo t = new Torneo();
		t.setNome("T");
		t.setAnno(2026);
		t.setDescrizione("x");
		t.aggiungiSquadra(s);
		tornei.saveAndFlush(t);
		List<Squadra> jf = squadre.findRoseByTorneoIdJoinFetch(t.getId());
		List<Squadra> eg = squadre.findRoseByTorneoIdEntityGraph(t.getId());
		assertAll(() -> assertEquals(1, jf.size()), () -> assertEquals(1, jf.get(0).getGiocatori().size()),
				() -> assertEquals(1, eg.size()), () -> assertEquals(1, eg.get(0).getGiocatori().size()));
	}
}
