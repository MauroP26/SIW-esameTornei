package it.unirom3.siw.api.dto;

import java.time.LocalDateTime;
import it.unirom3.siw.model.StatoPartita;

public record PartitaApiDto(
    Long id,
    LocalDateTime dataOra,
    String luogo,
    int goalsHome,
    int goalsAway,
    StatoPartita stato,
    Long torneoId,
    SquadraSintesiApiDto squadraCasa,
    SquadraSintesiApiDto squadraOspite,
    String arbitro
) {}
