package com.bolsadeideas.springboot.backend.apirest.auth;

import java.time.Duration;
import java.util.UUID;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;


@Configuration
public class AuthorizationServerConfig {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.security.oauth2.redirect-uri}")
	 private String appFrontendUrlRedireccion;

    // Cliente OAuth2 registrado (angularapp)
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient angularClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("angularapp")
            .clientSecret(passwordEncoder.encode("12345"))
            .scope("read")
            .scope("write")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
         
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri(appFrontendUrlRedireccion)
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofHours(12))
                .build())
            .build();

        return new InMemoryRegisteredClientRepository(angularClient);
    }
  

   
}



