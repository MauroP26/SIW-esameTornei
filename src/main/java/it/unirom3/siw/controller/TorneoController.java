package it.unirom3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.unirom3.siw.service.TorneoService;
import it.unirom3.siw.service.SquadraService;

@Controller
public class TorneoController {

	private TorneoService torneoService;
	private SquadraService squadraService;

	public TorneoController(TorneoService t, SquadraService s) {
		this.torneoService = t;
		this.squadraService = s;
	}

	@GetMapping("/tornei")
	public String getTornei(Model model) {
		model.addAttribute("tornei", torneoService.getTuttiTornei());
		return "tornei";
	}

	@GetMapping("/tornei/{id}")
	public String dettaglioTorneo(@PathVariable Long id, Model model) {
		model.addAttribute("torneo", torneoService.getTorneoById(id));
		model.addAttribute("classifica", torneoService.getClassifica(id));
		model.addAttribute("calendario", torneoService.getTorneoById(id).getPartite().stream()
				.sorted(java.util.Comparator.comparing(it.unirom3.siw.model.Partita::getDataOra)).toList());
		model.addAttribute("tutteSquadre", squadraService.getTutteSquadre());
		return "torneo";
	}

}
