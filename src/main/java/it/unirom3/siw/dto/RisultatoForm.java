package it.unirom3.siw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RisultatoForm {
	@NotNull(message = "Inserisci i gol della squadra di casa")
	@Min(value = 0, message = "I gol non possono essere negativi")
	private Integer goalCasa;

	@NotNull(message = "Inserisci i gol della squadra ospite")
	@Min(value = 0, message = "I gol non possono essere negativi")
	private Integer goalOspiti;

	public Integer getGoalCasa() {
		return goalCasa;
	}

	public void setGoalCasa(Integer goalCasa) {
		this.goalCasa = goalCasa;
	}

	public Integer getGoalOspiti() {
		return goalOspiti;
	}

	public void setGoalOspiti(Integer goalOspiti) {
		this.goalOspiti = goalOspiti;
	}
}
