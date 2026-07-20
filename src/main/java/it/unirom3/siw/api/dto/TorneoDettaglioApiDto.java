package it.unirom3.siw.api.dto;

import java.util.List;

public record TorneoDettaglioApiDto(
    Long id,
    String nome,
    int anno,
    String descrizione,
    List<SquadraSintesiApiDto> squadre
) {}
