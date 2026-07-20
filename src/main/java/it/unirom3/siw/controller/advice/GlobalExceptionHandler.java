package it.unirom3.siw.controller.advice;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RisorsaNonTrovataException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String risorsaNonTrovata(RisorsaNonTrovataException ex, HttpServletRequest request, Model model) {
		prepara(model, 404, "Risorsa non trovata", ex.getMessage(), request);
		return "error/404";
	}

	@ExceptionHandler({ RegolaBusinessException.class, IllegalStateException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	public String conflitto(RuntimeException ex, HttpServletRequest request, Model model) {
		prepara(model, 409, "Operazione non consentita", ex.getMessage(), request);
		return "error/errore";
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String vincoloDatabase(DataIntegrityViolationException ex, HttpServletRequest request, Model model) {
		prepara(model, 409, "Dati in conflitto", "L'operazione viola un vincolo sui dati presenti.", request);
		return "error/errore";
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String accessoNegato(AccessDeniedException ex, HttpServletRequest request, Model model) {
		prepara(model, 403, "Accesso negato", ex.getMessage(), request);
		return "error/403";
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String richiestaNonValida(IllegalArgumentException ex, HttpServletRequest request, Model model) {
		prepara(model, 400, "Richiesta non valida", ex.getMessage(), request);
		return "error/errore";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String erroreGenerico(Exception ex, HttpServletRequest request, Model model) {
		prepara(model, 500, "Errore interno", "Si è verificato un errore inatteso. Riprova più tardi.", request);
		return "error/errore";
	}

	private void prepara(Model model, int stato, String titolo, String messaggio, HttpServletRequest request) {
		model.addAttribute("stato", stato);
		model.addAttribute("titoloErrore", titolo);
		model.addAttribute("messaggioErrore", messaggio == null || messaggio.isBlank() ? titolo : messaggio);
		model.addAttribute("percorso", request.getRequestURI());
		model.addAttribute("dataOra", LocalDateTime.now());
	}
}
