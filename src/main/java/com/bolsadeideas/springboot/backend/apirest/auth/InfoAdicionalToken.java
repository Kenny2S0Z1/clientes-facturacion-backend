package com.bolsadeideas.springboot.backend.apirest.auth;


import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import org.springframework.stereotype.Component;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Usuario;
import com.bolsadeideas.springboot.backend.apirest.models.services.IUsuarioService;

@Component
public class InfoAdicionalToken  {
	
	@Autowired
	IUsuarioService usuarioService;
	@Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
        	
        	Usuario usuario= usuarioService.findByUsername2(context.getPrincipal().getName());
        	;
            // Solo agregar claims a access token
            if (context.getTokenType().getValue().equals("access_token")) {
                
                if(usuario!=null) {
                	 context.getClaims().claim("nombre", usuario.getNombre());
                	 context.getClaims().claim("apellido", usuario.getApellido());
                	 context.getClaims().claim("email", usuario.getEmail());
                	 context.getClaims().claim("authorities",usuario.getRoles()
                			                                 .stream()
                			                                 .map(r->r.getNombre())
                			                                 .filter(Objects::nonNull)
                			                                 .collect(Collectors.toList())
                			 
                			 );
                	
                }
            }
        };
    }
	
}
