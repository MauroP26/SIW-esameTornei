package it.unirom3.siw.api.dto;

import java.time.LocalDate;
import it.unirom3.siw.model.RuoloGiocatore;

public record GiocatoreApiDto(
    Long id,
    String nome,
    String cognome,
    LocalDate dataNascita,
    RuoloGiocatore ruolo,
    double altezza
) {}
