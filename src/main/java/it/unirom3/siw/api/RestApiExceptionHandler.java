package it.unirom3.siw.api;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.unirom3.siw.api.dto.ApiErroreDto;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import jakarta.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "it.unirom3.siw.api")
public class RestApiExceptionHandler {

	@ExceptionHandler(RisorsaNonTrovataException.class)
	public ResponseEntity<ApiErroreDto> notFound(RisorsaNonTrovataException ex, HttpServletRequest request) {
		return risposta(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(RegolaBusinessException.class)
	public ResponseEntity<ApiErroreDto> business(RegolaBusinessException ex, HttpServletRequest request) {
		return risposta(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
	}

	private ResponseEntity<ApiErroreDto> risposta(HttpStatus status, String messaggio, String percorso) {
		return ResponseEntity.status(status).body(
				new ApiErroreDto(LocalDateTime.now(), status.value(), status.getReasonPhrase(), messaggio, percorso));
	}
}
