package it.unirom3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Squadra {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Il nome della squadra è obbligatorio")
	@Size(max = 100)
	private String nome;
	@Min(1800)
	@Max(2100)
	private int annoFondazione;
	@NotBlank(message = "La città è obbligatoria")
	@Size(max = 100)
	private String citta;

	@OneToMany(mappedBy = "squadra", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Giocatore> giocatori = new ArrayList<>();

	@ManyToMany(mappedBy = "squadre")
	private List<Torneo> tornei = new ArrayList<>();

	public void aggiungiGiocatore(Giocatore giocatore) {
		giocatori.add(giocatore);
		giocatore.setSquadra(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getAnnoFondazione() {
		return annoFondazione;
	}

	public void setAnnoFondazione(int annoFondazione) {
		this.annoFondazione = annoFondazione;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public List<Giocatore> getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(List<Giocatore> giocatori) {
		this.giocatori = giocatori == null ? new ArrayList<>() : giocatori;
	}

	public List<Torneo> getTornei() {
		return tornei;
	}

	public void setTornei(List<Torneo> tornei) {
		this.tornei = tornei == null ? new ArrayList<>() : tornei;
	}
}
