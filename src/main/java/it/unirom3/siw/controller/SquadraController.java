package it.unirom3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.service.SquadraService;

@Controller
public class SquadraController {

	private SquadraService squadraService;

	public SquadraController(SquadraService s) {
		this.squadraService = s;
	}

	@GetMapping("/squadre")
	public String getSquadre(Model model) {

		model.addAttribute("squadre", squadraService.getTutteSquadre());
		return "squadre";

	}

	@GetMapping("/squadre/{id}")
	public String getSquadra(@PathVariable Long id, Model model) {

		Squadra squadra = squadraService.getSquadraById(id);
		model.addAttribute("squadra", squadra);
		return "squadra";

	}
}
