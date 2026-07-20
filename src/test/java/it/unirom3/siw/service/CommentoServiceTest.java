package it.unirom3.siw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.model.*;
import it.unirom3.siw.repository.*;

class CommentoServiceTest {
	CommentoRepository commenti = mock(CommentoRepository.class);
	PartitaRepository partite = mock(PartitaRepository.class);
	UtenteRepository utenti = mock(UtenteRepository.class);
	CommentoService service = new CommentoService(commenti, partite, utenti);

	@Test
	void aggiungeCommentoNormalizzato() {
		Partita p = new Partita();
		p.setId(1L);
		Utente u = utente("mario");
		when(commenti.findByPartitaIdAndAutoreCredUsername(1L, "mario")).thenReturn(Optional.empty());
		when(partite.findById(1L)).thenReturn(Optional.of(p));
		when(utenti.findByCredUsername("mario")).thenReturn(Optional.of(u));
		when(commenti.save(any())).thenAnswer(i -> i.getArgument(0));
		Commento c = service.aggiungiCommento(1L, "mario", "  bello  ");
		assertEquals("bello", c.getTesto());
		assertSame(u, c.getAutore());
		assertTrue(p.getCommenti().contains(c));
	}

	@Test
	void impedisceSecondoCommento() {
		when(commenti.findByPartitaIdAndAutoreCredUsername(1L, "mario")).thenReturn(Optional.of(new Commento()));
		assertThrows(RegolaBusinessException.class, () -> service.aggiungiCommento(1L, "mario", "testo"));
	}

	@Test
	void modificaSoloAutore() {
		Commento c = new Commento();
		c.setId(2L);
		c.setAutore(utente("lucia"));
		when(commenti.findById(2L)).thenReturn(Optional.of(c));
		assertThrows(AccessDeniedException.class, () -> service.modificaCommento(2L, "mario", "nuovo"));
	}

	@Test
	void rifiutaTestoVuoto() {
		assertThrows(RegolaBusinessException.class, () -> service.aggiungiCommento(1L, "mario", "   "));
	}

	private Utente utente(String username) {
		Credenziali cr = new Credenziali();
		cr.setUsername(username);
		cr.setPassword("x");
		cr.setRole(Ruolo.USER);
		Utente u = new Utente();
		u.setCred(cr);
		return u;
	}
}
