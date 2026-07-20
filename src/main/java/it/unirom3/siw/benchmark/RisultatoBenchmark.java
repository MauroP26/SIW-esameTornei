package it.unirom3.siw.benchmark;

public record RisultatoBenchmark(
        int ripetizione,
        StrategiaAccesso strategia,
        long tempoNanosecondi,
        long querySql,
        int numeroSquadre,
        int numeroGiocatori,
        long checksum) {

    public double tempoMillisecondi() {
        return tempoNanosecondi / 1_000_000.0;
    }
}
