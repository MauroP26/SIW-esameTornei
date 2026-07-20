package it.unirom3.siw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import it.unirom3.siw.model.*;
import it.unirom3.siw.repository.TorneoRepository;

class TorneoServiceTest {
	@Test
	void calcolaClassificaIgnorandoPartiteProgrammate() {
        TorneoRepository repo=mock(TorneoRepository.class); TorneoService service=new TorneoService(repo);
        Squadra a=squadra(1L,"A"), b=squadra(2L,"B"), c=squadra(3L,"C");
        Torneo t=new Torneo(); t.setId(10L); t.setSquadre(new ArrayList<>(List.of(a,b,c)));
        t.setPartite(new ArrayList<>(List.of(partita(a,b,2,1,StatoPartita.PLAYED), partita(b,c,0,0,StatoPartita.PLAYED), partita(c,a,9,0,StatoPartita.SCHEDULED))));
        when(repo.findById(10L)).thenReturn(Optional.of(t));
        List<EntrataInClassifica> classifica=service.getClassifica(10L);
        assertAll(() -> assertEquals("A",classifica.get(0).getSquadra().getNome()), () -> assertEquals(3,classifica.get(0).getPunti()),
            () -> assertEquals("C",classifica.get(1).getSquadra().getNome()), () -> assertEquals(1,classifica.get(1).getPunti()),
            () -> assertEquals(1,classifica.get(1).getPareggi()), () -> assertEquals(3,classifica.size()));
    }
    private Squadra squadra(Long id,String nome){Squadra s=new Squadra();s.setId(id);s.setNome(nome);return s;}
    private Partita partita(Squadra h,Squadra a,int gh,int ga,StatoPartita stato){Partita p=new Partita();p.setHome(h);p.setAway(a);p.setGoalsHome(gh);p.setGoalsAway(ga);p.setStato(stato);return p;}
}
