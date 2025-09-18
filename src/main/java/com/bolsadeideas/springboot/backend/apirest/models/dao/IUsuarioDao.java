package com.bolsadeideas.springboot.backend.apirest.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
           
	Usuario findByUsername(String username);
	
	@Query("select u from Usuario u LEFT JOIN FETCH u.roles where u.username=?1")
	Usuario findByUsername2(String username);
}
