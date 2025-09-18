package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Factura;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Producto;
import com.bolsadeideas.springboot.backend.apirest.models.response.ResponseErrorForm;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/api")
public class FacturaRestController {
    @Autowired       
	private IClienteService clienteService;
	
    @Secured({"ROLE_USER","ROLE_ADMIN"})
	@GetMapping("/facturas/{id}")
	public ResponseEntity<?> mostrarPorId(@PathVariable Long id) {
		Factura factura = null;
		Map<String, Object> response = new HashMap<>();
		try {
			factura = clienteService.findFacturaById(id);
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (factura == null) {
			response.put("message", "La factura ID".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		response.put("message", "Factura encontrado en la base de datos");
		response.put("success", true);
		response.put("factura", factura);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}
	
    @Secured({"ROLE_ADMIN"})
	@DeleteMapping("/facturas/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
		
			clienteService.deleteFactura(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete");
			response.put("error", e.getMessage().concat(" :").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);

	}
	
    @Secured({"ROLE_ADMIN"})
	@GetMapping("/facturas/filtrar-productos/{term}")
	public ResponseEntity<?> filtrarProductos(@PathVariable String term) {
		List<Producto> productos = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		try {
			productos = clienteService.findProductoByNombre(term);
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (productos.size()==0) {
			response.put("message", "No se han encontrado coincidencias");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		response.put("message", productos.size() + "coindidencias encontradas");
		response.put("success", true);
		response.put("productos", productos);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}
	
    @Secured({"ROLE_ADMIN"})
	@PostMapping("/facturas")
	public ResponseEntity<?> create(@Valid @RequestBody Factura factura, BindingResult result) {
		Factura facturaNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {

			List<ResponseErrorForm> errors = result.getFieldErrors().stream()
					.map(f -> new ResponseErrorForm(f.getField(), f.getDefaultMessage())).toList();

			response.put("message", "Error formulario");
			response.put("errors", errors);
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			facturaNew = clienteService.saveFactura(factura);
		} catch (DataAccessException e) {
			response.put("message", "Error al realiza el insert");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("message", "Factura creada correctamente");
		response.put("success", true);
		response.put("factura", facturaNew);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	


}
