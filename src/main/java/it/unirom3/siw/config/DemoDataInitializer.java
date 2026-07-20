package it.unirom3.siw.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.unirom3.siw.model.Arbitro;
import it.unirom3.siw.model.Credenziali;
import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.model.Ruolo;
import it.unirom3.siw.model.RuoloGiocatore;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.StatoPartita;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.model.Utente;
import it.unirom3.siw.repository.ArbitroRepository;
import it.unirom3.siw.repository.PartitaRepository;
import it.unirom3.siw.repository.SquadraRepository;
import it.unirom3.siw.repository.TorneoRepository;
import it.unirom3.siw.repository.UtenteRepository;

@Configuration
@Profile("!test")
public class DemoDataInitializer {

	private static final String[] NOMI_GIOCATORI = { "Luca", "Marco", "Andrea", "Matteo", "Davide", "Alessandro",
			"Federico", "Simone", "Francesco", "Antonio", "Stefano" };

	private static final String[] COGNOMI_GIOCATORI = { "Rossi", "Bianchi", "Romano", "Ricci", "Marino", "Greco",
			"Bruno", "Gallo", "Conti", "De Luca", "Moretti" };

	@Bean
	@Order(10)
	CommandLineRunner demoData(TorneoRepository torneoRepository, SquadraRepository squadraRepository,
			ArbitroRepository arbitroRepository, PartitaRepository partitaRepository, UtenteRepository utenteRepository,
			PasswordEncoder passwordEncoder) {

		return args -> {

			if (torneoRepository.count() > 0) {
				return;
			}

			/*
			 * ============================================================ ARBITRI
			 * ============================================================
			 */

			Arbitro arbitroRoma = creaArbitro("Marco", "Rossi", "ARB100");

			Arbitro arbitroMilano = creaArbitro("Paolo", "Bianchi", "ARB200");

			Arbitro arbitroNapoli = creaArbitro("Giuseppe", "Esposito", "ARB300");

			arbitroRepository.saveAll(List.of(arbitroRoma, arbitroMilano, arbitroNapoli));

			/*
			 * ============================================================ TORNEO 1: COPPA
			 * ESTATE 2026 ============================================================
			 */

			Squadra romaAmatori = creaSquadraConRosa("Roma Amatori", 2010, "Roma", 0);

			Squadra milanoUnited = creaSquadraConRosa("Milano United", 2012, "Milano", 1);

			Squadra napoliFriends = creaSquadraConRosa("Napoli Friends", 2015, "Napoli", 2);

			Squadra torinoSport = creaSquadraConRosa("Torino Sport", 2011, "Torino", 3);

			squadraRepository.saveAll(List.of(romaAmatori, milanoUnited, napoliFriends, torinoSport));

			Torneo coppaEstate = creaTorneo("Coppa Estate 2026", 2026,
					"Torneo amatoriale estivo con squadre provenienti " + "da diverse città italiane.",
					List.of(romaAmatori, milanoUnited, napoliFriends, torinoSport));

			torneoRepository.save(coppaEstate);

			/*
			 * ============================================================ TORNEO 2: TROFEO
			 * DELLE CITTÀ 2026 ============================================================
			 */

			Squadra firenzeCalcio = creaSquadraConRosa("Firenze Calcio", 2009, "Firenze", 4);

			Squadra bolognaAcademy = creaSquadraConRosa("Bologna Academy", 2014, "Bologna", 5);

			Squadra genovaClub = creaSquadraConRosa("Genova Club", 2008, "Genova", 6);

			Squadra veneziaLaguna = creaSquadraConRosa("Venezia Laguna", 2016, "Venezia", 7);

			squadraRepository.saveAll(List.of(firenzeCalcio, bolognaAcademy, genovaClub, veneziaLaguna));

			Torneo trofeoCitta = creaTorneo("Trofeo delle Città 2026", 2026,
					"Competizione dedicata alle rappresentative amatoriali " + "delle principali città italiane.",
					List.of(firenzeCalcio, bolognaAcademy, genovaClub, veneziaLaguna));

			torneoRepository.save(trofeoCitta);

			/*
			 * ============================================================ TORNEO 3: WINTER
			 * LEAGUE 2026 ============================================================
			 */

			Squadra palermoStars = creaSquadraConRosa("Palermo Stars", 2013, "Palermo", 8);

			Squadra bariAthletic = creaSquadraConRosa("Bari Athletic", 2010, "Bari", 9);

			Squadra perugiaTeam = creaSquadraConRosa("Perugia Team", 2017, "Perugia", 10);

			Squadra cagliariIsland = creaSquadraConRosa("Cagliari Island", 2012, "Cagliari", 11);

			squadraRepository.saveAll(List.of(palermoStars, bariAthletic, perugiaTeam, cagliariIsland));

			Torneo winterLeague = creaTorneo("Winter League 2026", 2026,
					"Campionato invernale amatoriale con fase a gironi " + "e classifica a punti.",
					List.of(palermoStars, bariAthletic, perugiaTeam, cagliariIsland));

			torneoRepository.save(winterLeague);

			/*
			 * ============================================================ PARTITE DELLA
			 * COPPA ESTATE ============================================================
			 */

			salvaPartitaGiocata(partitaRepository, coppaEstate, romaAmatori, milanoUnited, arbitroRoma,
					LocalDateTime.of(2026, 5, 10, 18, 0), "Campo Roma Sud", 2, 1);

			salvaPartitaGiocata(partitaRepository, coppaEstate, napoliFriends, torinoSport, arbitroMilano,
					LocalDateTime.of(2026, 5, 12, 20, 30), "Campo Napoli Centro", 0, 0);

			salvaPartitaGiocata(partitaRepository, coppaEstate, milanoUnited, napoliFriends, arbitroNapoli,
					LocalDateTime.of(2026, 6, 4, 19, 30), "Arena Milano Nord", 3, 2);

			salvaPartitaProgrammata(partitaRepository, coppaEstate, torinoSport, romaAmatori, arbitroRoma,
					LocalDateTime.of(2026, 8, 20, 20, 30), "Centro Sportivo Torino");

			/*
			 * ============================================================ PARTITE DEL
			 * TROFEO DELLE CITTÀ
			 * ============================================================
			 */

			salvaPartitaGiocata(partitaRepository, trofeoCitta, firenzeCalcio, bolognaAcademy, arbitroMilano,
					LocalDateTime.of(2026, 3, 15, 16, 0), "Stadio Comunale Firenze", 1, 1);

			salvaPartitaGiocata(partitaRepository, trofeoCitta, genovaClub, veneziaLaguna, arbitroRoma,
					LocalDateTime.of(2026, 3, 18, 18, 30), "Campo Genova Levante", 2, 0);

			salvaPartitaGiocata(partitaRepository, trofeoCitta, bolognaAcademy, genovaClub, arbitroNapoli,
					LocalDateTime.of(2026, 4, 8, 21, 0), "Centro Sportivo Bologna", 1, 3);

			salvaPartitaProgrammata(partitaRepository, trofeoCitta, veneziaLaguna, firenzeCalcio, arbitroMilano,
					LocalDateTime.of(2026, 9, 5, 18, 0), "Campo Venezia Mestre");

			/*
			 * ============================================================ PARTITE DELLA
			 * WINTER LEAGUE ============================================================
			 */

			salvaPartitaGiocata(partitaRepository, winterLeague, palermoStars, bariAthletic, arbitroNapoli,
					LocalDateTime.of(2026, 1, 17, 17, 30), "Centro Sportivo Palermo", 4, 2);

			salvaPartitaGiocata(partitaRepository, winterLeague, perugiaTeam, cagliariIsland, arbitroRoma,
					LocalDateTime.of(2026, 1, 20, 19, 0), "Campo Perugia Ovest", 1, 0);

			salvaPartitaGiocata(partitaRepository, winterLeague, bariAthletic, perugiaTeam, arbitroMilano,
					LocalDateTime.of(2026, 2, 7, 18, 30), "Arena Bari", 2, 2);

			salvaPartitaProgrammata(partitaRepository, winterLeague, cagliariIsland, palermoStars, arbitroNapoli,
					LocalDateTime.of(2026, 12, 6, 20, 30), "Centro Sportivo Cagliari");

			/*
			 * ============================================================ UTENTI
			 * ============================================================
			 */

			utenteRepository.save(creaUtente("admin", "admin", Ruolo.ADMIN, passwordEncoder));

			utenteRepository.save(creaUtente("mario", "password", Ruolo.USER, passwordEncoder));
		};
	}

	private Torneo creaTorneo(String nome, int anno, String descrizione, List<Squadra> squadre) {

		Torneo torneo = new Torneo();

		torneo.setNome(nome);
		torneo.setAnno(anno);
		torneo.setDescrizione(descrizione);

		squadre.forEach(torneo::aggiungiSquadra);

		return torneo;
	}

	private Squadra creaSquadraConRosa(String nome, int annoFondazione, String citta, int indiceSquadra) {

		Squadra squadra = new Squadra();

		squadra.setNome(nome);
		squadra.setAnnoFondazione(annoFondazione);
		squadra.setCitta(citta);

		for (int i = 0; i < 11; i++) {

			String nomeGiocatore = NOMI_GIOCATORI[(i + indiceSquadra) % NOMI_GIOCATORI.length];

			String cognomeGiocatore = COGNOMI_GIOCATORI[(i + indiceSquadra * 2) % COGNOMI_GIOCATORI.length];

			cognomeGiocatore = cognomeGiocatore + " " + citta;

			LocalDate dataNascita = LocalDate.of(1994 + ((i + indiceSquadra) % 10), (i % 12) + 1,
					((i * 2 + indiceSquadra) % 27) + 1);

			RuoloGiocatore ruolo = determinaRuolo(i);

			double altezza = 1.72 + ((i + indiceSquadra) % 11) * 0.02;

			aggiungiGiocatore(squadra, nomeGiocatore, cognomeGiocatore, dataNascita, ruolo, altezza);
		}

		return squadra;
	}

	private RuoloGiocatore determinaRuolo(int indiceGiocatore) {

		if (indiceGiocatore == 0) {
			return RuoloGiocatore.PORTIERE;
		}

		if (indiceGiocatore <= 4) {
			return RuoloGiocatore.DIFENSORE;
		}

		if (indiceGiocatore <= 8) {
			return RuoloGiocatore.CENTROCAMPISTA;
		}

		return RuoloGiocatore.ATTACCANTE;
	}

	private void aggiungiGiocatore(Squadra squadra, String nome, String cognome, LocalDate dataNascita,
			RuoloGiocatore ruolo, double altezza) {

		Giocatore giocatore = new Giocatore();

		giocatore.setNome(nome);
		giocatore.setCognome(cognome);
		giocatore.setDataNascita(dataNascita);
		giocatore.setRuolo(ruolo);
		giocatore.setAltezza(altezza);

		squadra.aggiungiGiocatore(giocatore);
	}

	private Arbitro creaArbitro(String nome, String cognome, String codiceArbitrale) {

		Arbitro arbitro = new Arbitro();

		arbitro.setNome(nome);
		arbitro.setCognome(cognome);
		arbitro.setCodiceArbitrale(codiceArbitrale);

		return arbitro;
	}

	private Partita creaPartita(Torneo torneo, Squadra squadraCasa, Squadra squadraOspite, Arbitro arbitro,
			LocalDateTime dataOra, String luogo) {

		Partita partita = new Partita();

		partita.setTorneo(torneo);
		partita.setHome(squadraCasa);
		partita.setAway(squadraOspite);
		partita.setArbitro(arbitro);
		partita.setDataOra(dataOra);
		partita.setLuogo(luogo);
		partita.setStato(StatoPartita.SCHEDULED);

		return partita;
	}

	private void salvaPartitaGiocata(PartitaRepository partitaRepository, Torneo torneo, Squadra squadraCasa,
			Squadra squadraOspite, Arbitro arbitro, LocalDateTime dataOra, String luogo, int goalCasa, int goalOspiti) {

		Partita partita = creaPartita(torneo, squadraCasa, squadraOspite, arbitro, dataOra, luogo);

		partita.setGoalsHome(goalCasa);
		partita.setGoalsAway(goalOspiti);
		partita.setStato(StatoPartita.PLAYED);

		partitaRepository.save(partita);
	}

	private void salvaPartitaProgrammata(PartitaRepository partitaRepository, Torneo torneo, Squadra squadraCasa,
			Squadra squadraOspite, Arbitro arbitro, LocalDateTime dataOra, String luogo) {

		Partita partita = creaPartita(torneo, squadraCasa, squadraOspite, arbitro, dataOra, luogo);

		partitaRepository.save(partita);
	}

	private Utente creaUtente(String username, String password, Ruolo ruolo, PasswordEncoder passwordEncoder) {

		Credenziali credenziali = new Credenziali();

		credenziali.setUsername(username);
		credenziali.setPassword(passwordEncoder.encode(password));
		credenziali.setRole(ruolo);

		Utente utente = new Utente();
		utente.setCred(credenziali);

		return utente;
	}
}