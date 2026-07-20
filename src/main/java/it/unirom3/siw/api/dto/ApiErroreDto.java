package it.unirom3.siw.api.dto;

import java.time.LocalDateTime;

public record ApiErroreDto(
    LocalDateTime timestamp,
    int status,
    String errore,
    String messaggio,
    String percorso
) {}
