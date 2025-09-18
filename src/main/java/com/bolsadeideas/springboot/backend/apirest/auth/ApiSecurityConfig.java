package com.bolsadeideas.springboot.backend.apirest.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.util.pattern.PathPatternParser;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

@Configuration
public class ApiSecurityConfig {

	@Autowired
	private JwtDecoder jwtDecoder;
	
	 @Value("${app.frontend.url}")
	 private String appFrontendUrl;
	
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
	    http.securityMatcher("/api/**")
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(HttpMethod.GET, "/api/clientes", "/api/clientes/page/**",
	                             "/api/uploads/img/**", "/images/**").permitAll()
	       
	            .anyRequest().authenticated()
	        )
	        .oauth2ResourceServer(oauth2 -> oauth2
	            .jwt(jwt -> jwt
	                .decoder(jwtDecoder)
	                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // <- aquí
	            )
	        );

	    return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {
	    // Aplica la configuración default del Authorization Server
	    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

	    // Form login para la ruta /login
	    http.formLogin(form -> form.loginPage("/login").permitAll());

	    
	    // Asegura que este SecurityFilterChain maneje /logout y rutas OAuth2
	    http.securityMatcher("/logout", "/oauth2/**");
	    http.csrf(csrf -> csrf
	    	    .ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults()
	    	    		.matcher(HttpMethod.POST, "/logout"))
	    	);
	    http.logout(logout -> logout
	    	    .logoutUrl("/logout")
	    	    .invalidateHttpSession(true)
	    	    .deleteCookies("JSESSIONID")
	    	    .logoutSuccessHandler((request, response, authentication) -> {
	    	        response.setStatus(HttpServletResponse.SC_OK);
	    	    })
	    	);


	    return http.build();
	}
	
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
	    authoritiesConverter.setAuthoritiesClaimName("authorities"); // claim de tu JWT
	    authoritiesConverter.setAuthorityPrefix("");// elimina el prefijo SCOPE_
	    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
	    converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
	    return converter;
	}
	

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOriginPatterns(Arrays.asList(appFrontendUrl));
	    config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
	    config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Accept","X-Requested-With","Origin"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
	    bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // intercepta antes que cualquier filtro de Spring Security
	    return bean;
	}
}
