package it.unirom3.siw.api.dto;

import java.util.List;

public record SquadraDettaglioApiDto(
    Long id,
    String nome,
    int annoFondazione,
    String citta,
    List<GiocatoreApiDto> giocatori
) {}
