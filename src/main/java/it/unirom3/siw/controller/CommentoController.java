package it.unirom3.siw.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.unirom3.siw.dto.CommentoForm;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.model.Commento;
import it.unirom3.siw.service.CommentoService;
import jakarta.validation.Valid;

@Controller
public class CommentoController {
	private final CommentoService commentoService;

	public CommentoController(CommentoService commentoService) {
		this.commentoService = commentoService;
	}

	@PostMapping("/commenti/partite/{partitaId}")
	public String aggiungi(@PathVariable Long partitaId, @Valid @ModelAttribute CommentoForm form,
			BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			preparaErrore(bindingResult, form, redirectAttributes);
			return "redirect:/partite/" + partitaId;
		}
		try {
			commentoService.aggiungiCommento(partitaId, principal.getName(), form.getTesto());
			redirectAttributes.addFlashAttribute("successo", "Commento pubblicato");
		} catch (RegolaBusinessException ex) {
			redirectAttributes.addFlashAttribute("erroreCommento", ex.getMessage());
			redirectAttributes.addFlashAttribute("testoCommento", form.getTesto());
		}
		return "redirect:/partite/" + partitaId;
	}

	@PostMapping("/commenti/{commentoId}/modifica")
	public String modifica(@PathVariable Long commentoId, @Valid @ModelAttribute CommentoForm form,
			BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
		Long partitaId = commentoService.getPartitaIdDelCommento(commentoId);
		if (bindingResult.hasErrors()) {
			preparaErrore(bindingResult, form, redirectAttributes);
			return "redirect:/partite/" + partitaId;
		}
		Commento commento = commentoService.modificaCommento(commentoId, principal.getName(), form.getTesto());
		redirectAttributes.addFlashAttribute("successo", "Commento modificato");
		return "redirect:/partite/" + commento.getPartita().getId();
	}

	@PostMapping("/commenti/{commentoId}/elimina")
	public String elimina(@PathVariable Long commentoId, Principal principal, RedirectAttributes redirectAttributes) {
		Long partitaId = commentoService.eliminaCommento(commentoId, principal.getName());
		redirectAttributes.addFlashAttribute("successo", "Commento eliminato");
		return "redirect:/partite/" + partitaId;
	}

	private void preparaErrore(BindingResult bindingResult, CommentoForm form, RedirectAttributes redirectAttributes) {
		String messaggio = bindingResult.getAllErrors().isEmpty() ? "Commento non valido"
				: bindingResult.getAllErrors().get(0).getDefaultMessage();
		redirectAttributes.addFlashAttribute("erroreCommento", messaggio);
		redirectAttributes.addFlashAttribute("testoCommento", form.getTesto());
	}
}
