package com.bolsadeideas.springboot.backend.apirest.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Producto;
import java.util.List;


public interface IProductoDao extends CrudRepository<Producto, Long> {
    
	@Query("SELECT p FROM Producto p WHERE p.nombre like %?1%")
	List<Producto> findByNombre(String term);
}
