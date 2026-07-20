package it.unirom3.siw.api.dto;

public record ClassificaApiDto(
    int posizione,
    SquadraSintesiApiDto squadra,
    int punti,
    int vittorie,
    int pareggi,
    int sconfitte,
    int goalFatti,
    int goalSubiti,
    int differenzaReti
) {}
