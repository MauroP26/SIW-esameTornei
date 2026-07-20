package it.unirom3.siw.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebSecurityIntegrationTest {
	@Autowired
	MockMvc mvc;

	@Test
	void apiPubblicaAccessibileAnonimo() throws Exception {
		mvc.perform(get("/api/tornei")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"));
	}

	@Test
	void adminAnonimoVieneReindirizzatoAlLogin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(roles = "USER")
	void userNonAccedeAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void adminAccedePannello() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk()).andExpect(view().name("admin/dashboard"));
	}

	@Test
	void apiIdInesistenteRestituisce404Json() throws Exception {
		mvc.perform(get("/api/tornei/999999")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}
