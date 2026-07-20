package it.unirom3.siw.dto;

import java.time.LocalDate;
import it.unirom3.siw.model.RuoloGiocatore;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public class GiocatoreForm {
	@NotBlank(message = "Il nome è obbligatorio")
	@Size(max = 80)
	private String nome;
	@NotBlank(message = "Il cognome è obbligatorio")
	@Size(max = 80)
	private String cognome;
	@NotNull(message = "La data di nascita è obbligatoria")
	@Past(message = "La data deve essere nel passato")
	private LocalDate dataNascita;
	@NotNull(message = "Il ruolo è obbligatorio")
	private RuoloGiocatore ruolo;
	@DecimalMin(value = "1.20", message = "Altezza minima 1.20 m")
	@DecimalMax(value = "2.50", message = "Altezza massima 2.50 m")
	private double altezza;
	@NotNull(message = "La squadra è obbligatoria")
	private Long squadraId;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public LocalDate getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	public RuoloGiocatore getRuolo() {
		return ruolo;
	}

	public void setRuolo(RuoloGiocatore ruolo) {
		this.ruolo = ruolo;
	}

	public double getAltezza() {
		return altezza;
	}

	public void setAltezza(double altezza) {
		this.altezza = altezza;
	}

	public Long getSquadraId() {
		return squadraId;
	}

	public void setSquadraId(Long squadraId) {
		this.squadraId = squadraId;
	}
}
