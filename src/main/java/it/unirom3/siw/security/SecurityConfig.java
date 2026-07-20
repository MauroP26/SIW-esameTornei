package it.unirom3.siw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/login", "/tornei", "/tornei/**", "/squadre", "/squadre/**", "/partite",
						"/partite/**", "/api/**", "/react-app/**", "/css/**", "/js/**", "/images/**", "/app.css")
				.permitAll().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**", "/commenti/**")
				.hasAnyRole("USER", "ADMIN").anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/").permitAll());
		return http.build();
	}
}
