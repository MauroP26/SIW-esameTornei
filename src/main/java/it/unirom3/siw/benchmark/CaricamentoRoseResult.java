package it.unirom3.siw.benchmark;

/**
 * Piccolo risultato applicativo usato per impedire che il benchmark misuri
 * soltanto il recupero delle Squadra senza accedere davvero ai Giocatore.
 */
public record CaricamentoRoseResult(
        int numeroSquadre,
        int numeroGiocatori,
        long checksum) {
}
