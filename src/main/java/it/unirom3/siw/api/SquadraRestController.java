package it.unirom3.siw.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unirom3.siw.api.dto.SquadraDettaglioApiDto;

@RestController
@RequestMapping(value = "/api/squadre", produces = MediaType.APPLICATION_JSON_VALUE)
public class SquadraRestController {
	private final ApiQueryService apiQueryService;

	public SquadraRestController(ApiQueryService apiQueryService) {
		this.apiQueryService = apiQueryService;
	}

	@GetMapping("/{id}")
	public SquadraDettaglioApiDto dettaglio(@PathVariable Long id) {
		return apiQueryService.getSquadra(id);
	}
}
