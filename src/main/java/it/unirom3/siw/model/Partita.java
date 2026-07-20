package it.unirom3.siw.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Partita {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime dataOra;
	private String luogo;
	private int goalsHome;
	private int goalsAway;

	@Enumerated(EnumType.STRING)
	private StatoPartita stato;

	@ManyToOne(optional = false)
	private Torneo torneo;
	@ManyToOne(optional = false)
	private Squadra home;
	@ManyToOne(optional = false)
	private Squadra away;
	@ManyToOne(optional = false)
	private Arbitro arbitro;

	@OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Commento> commenti = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDataOra() {
		return dataOra;
	}

	public void setDataOra(LocalDateTime dataOra) {
		this.dataOra = dataOra;
	}

	public String getLuogo() {
		return luogo;
	}

	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}

	public int getGoalsHome() {
		return goalsHome;
	}

	public void setGoalsHome(int goalsHome) {
		this.goalsHome = goalsHome;
	}

	public int getGoalsAway() {
		return goalsAway;
	}

	public void setGoalsAway(int goalsAway) {
		this.goalsAway = goalsAway;
	}

	public StatoPartita getStato() {
		return stato;
	}

	public void setStato(StatoPartita stato) {
		this.stato = stato;
	}

	public Torneo getTorneo() {
		return torneo;
	}

	public void setTorneo(Torneo torneo) {
		this.torneo = torneo;
	}

	public Squadra getHome() {
		return home;
	}

	public void setHome(Squadra home) {
		this.home = home;
	}

	public Squadra getAway() {
		return away;
	}

	public void setAway(Squadra away) {
		this.away = away;
	}

	public Arbitro getArbitro() {
		return arbitro;
	}

	public void setArbitro(Arbitro arbitro) {
		this.arbitro = arbitro;
	}

	public List<Commento> getCommenti() {
		return commenti;
	}

	public void setCommenti(List<Commento> commenti) {
		this.commenti = commenti == null ? new ArrayList<>() : commenti;
	}
}
