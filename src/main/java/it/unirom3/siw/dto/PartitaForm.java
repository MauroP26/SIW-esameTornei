package it.unirom3.siw.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PartitaForm {
	@NotNull(message = "Seleziona un torneo")
	private Long torneoId;

	@NotNull(message = "Seleziona la squadra di casa")
	private Long squadraCasaId;

	@NotNull(message = "Seleziona la squadra ospite")
	private Long squadraOspiteId;

	@NotNull(message = "Seleziona un arbitro")
	private Long arbitroId;

	@NotBlank(message = "Il luogo è obbligatorio")
	@Size(max = 120, message = "Il luogo non può superare 120 caratteri")
	private String luogo;

	@NotNull(message = "La data e l'ora sono obbligatorie")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime dataOra;

	public Long getTorneoId() {
		return torneoId;
	}

	public void setTorneoId(Long torneoId) {
		this.torneoId = torneoId;
	}

	public Long getSquadraCasaId() {
		return squadraCasaId;
	}

	public void setSquadraCasaId(Long squadraCasaId) {
		this.squadraCasaId = squadraCasaId;
	}

	public Long getSquadraOspiteId() {
		return squadraOspiteId;
	}

	public void setSquadraOspiteId(Long squadraOspiteId) {
		this.squadraOspiteId = squadraOspiteId;
	}

	public Long getArbitroId() {
		return arbitroId;
	}

	public void setArbitroId(Long arbitroId) {
		this.arbitroId = arbitroId;
	}

	public String getLuogo() {
		return luogo;
	}

	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}

	public LocalDateTime getDataOra() {
		return dataOra;
	}

	public void setDataOra(LocalDateTime dataOra) {
		this.dataOra = dataOra;
	}
}
