package it.unirom3.siw.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unirom3.siw.api.dto.ClassificaApiDto;
import it.unirom3.siw.api.dto.PartitaApiDto;
import it.unirom3.siw.api.dto.TorneoApiDto;
import it.unirom3.siw.api.dto.TorneoDettaglioApiDto;

@RestController
@RequestMapping(value = "/api/tornei", produces = MediaType.APPLICATION_JSON_VALUE)
public class TorneoRestController {
	private final ApiQueryService apiQueryService;

	public TorneoRestController(ApiQueryService apiQueryService) {
		this.apiQueryService = apiQueryService;
	}

	@GetMapping
	public List<TorneoApiDto> elenco() {
		return apiQueryService.getTornei();
	}

	@GetMapping("/{id}")
	public TorneoDettaglioApiDto dettaglio(@PathVariable Long id) {
		return apiQueryService.getTorneo(id);
	}

	@GetMapping("/{id}/partite")
	public List<PartitaApiDto> calendario(@PathVariable Long id) {
		return apiQueryService.getCalendario(id);
	}

	@GetMapping("/{id}/classifica")
	public List<ClassificaApiDto> classifica(@PathVariable Long id) {
		return apiQueryService.getClassifica(id);
	}
}
