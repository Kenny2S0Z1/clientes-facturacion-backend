package com.bolsadeideas.springboot.backend.apirest.models.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.backend.apirest.models.dao.IUsuarioDao;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Usuario;



@Service
public class UsuarioService implements UserDetailsService,IUsuarioService{

	@Autowired
	private IUsuarioDao usuarioDao;
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 System.out.println("Intentando autenticar: " + username);
		Usuario usuario = usuarioDao.findByUsername(username);
		if(usuario==null) {
			throw new UsernameNotFoundException("Error no existe el usuario");
		}
		
		List<GrantedAuthority> authorities= usuario.getRoles()
				                              .stream()
				                              .map(r->new SimpleGrantedAuthority(r.getNombre()))
				                              .collect(Collectors.toList());
				                             
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(),true,true,true,authorities);
	}
	@Override
	@Transactional(readOnly = true)
	public Usuario findByUsername(String username) {
		// TODO Auto-generated method stub
		return usuarioDao.findByUsername(username);
	}
	@Override
	@Transactional(readOnly = true)
	public Usuario findByUsername2(String username) {
		// TODO Auto-generated method stub
		return usuarioDao.findByUsername2(username);
	}

}
