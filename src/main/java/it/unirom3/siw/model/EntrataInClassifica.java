package it.unirom3.siw.model;

public class EntrataInClassifica {

	private Squadra squadra;

	private int punti;
	private int vittorie;
	private int pareggi;
	private int sconfitte;

	private int goalFatti;
	private int goalSubiti;

	public EntrataInClassifica(Squadra s) {
		this.squadra = s;
	}

	public Squadra getSquadra() {
		return squadra;
	}

	public void setSquadra(Squadra squadra) {
		this.squadra = squadra;
	}

	public int getPunti() {
		return punti;
	}

	public void setPunti(int punti) {
		this.punti = punti;
	}

	public int getVittorie() {
		return vittorie;
	}

	public void setVittorie(int vittorie) {
		this.vittorie = vittorie;
	}

	public int getPareggi() {
		return pareggi;
	}

	public void setPareggi(int pareggi) {
		this.pareggi = pareggi;
	}

	public int getSconfitte() {
		return sconfitte;
	}

	public void setSconfitte(int sconfitte) {
		this.sconfitte = sconfitte;
	}

	public int getGoalFatti() {
		return goalFatti;
	}

	public void setGoalFatti(int goalFatti) {
		this.goalFatti = goalFatti;
	}

	public int getGoalSubiti() {
		return goalSubiti;
	}

	public void setGoalSubiti(int goalSubiti) {
		this.goalSubiti = goalSubiti;
	}

	public int getDifferenzaReti() {
		return this.goalFatti - this.goalSubiti;
	}

}