package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Region;
import com.bolsadeideas.springboot.backend.apirest.models.response.ResponseErrorForm;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;
import com.bolsadeideas.springboot.backend.apirest.models.services.IUploadFileService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api")

public class ClienteRestController {
	@Autowired
	IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadService;

	@GetMapping("/clientes")
	public List<Cliente> index() {

		return clienteService.findAll();
	}

	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page) {

		return clienteService.findAll(PageRequest.of(page, 4)); // 4 registros por pagina
	}
	
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> mostrarPorId(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cliente == null) {
			response.put("message", "El cliente ID".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		response.put("message", "Cliente encontrado en la base de datos");
		response.put("success", true);
		response.put("cliente", cliente);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}
	@Secured({ "ROLE_ADMIN" })
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		Cliente clienteNew = null;
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
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("message", "Error al realiza el insert");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("message", "Cliente obtenido correctamente");
		response.put("success", true);
		response.put("cliente", clienteNew);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured({ "ROLE_ADMIN"})
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, @PathVariable Long id, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		Cliente clienteActual = clienteService.findById(id);

		if (result.hasErrors()) {

			List<ResponseErrorForm> errors = result.getFieldErrors().stream()
					.map(f -> new ResponseErrorForm(f.getField(), f.getDefaultMessage())).toList();

			response.put("message", "Error formulario");
			response.put("errors", errors);
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if (clienteActual == null) {
			response.put("message", "Error: no se puedo encontrar, el cliente ID ".concat(id.toString()));
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}

		Cliente clienteUpdated = null;

		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			clienteActual.setRegion(cliente.getRegion());

			clienteUpdated = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar el update");
			response.put("success", false);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("message", "El cliente ha sido actualizado  con éxito!");
		response.put("success", true);
		response.put("cliente", clienteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}
	@Secured({ "ROLE_ADMIN"})
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {

			Cliente cliente = clienteService.findById(id);
			String nombreFotoAnterior = cliente.getFoto();
			uploadService.eliminar(nombreFotoAnterior);
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete");
			response.put("error", e.getMessage().concat(" :").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);

	}
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, 
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		Cliente cliente = clienteService.findById(id);

		if (!archivo.isEmpty()) {
			
			String nombreArchivo=null;
			try {
				nombreArchivo=uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("message", "Ocurrió un error al subir la imagen");
				response.put("success", false);
				return new ResponseEntity<Map<String, Object>>(response, 
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			String nombreFotoAnterior = cliente.getFoto();
			uploadService.eliminar(nombreFotoAnterior);
			
			cliente.setFoto(nombreArchivo);
			clienteService.save(cliente);

			response.put("cliente", cliente);
			response.put("message", "Has subido correctamente la imagen" + nombreArchivo);
			response.put("success", true);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> descargarFoto(@PathVariable String nombreFoto) {

		Path rutaArchivo = uploadService.getPath(nombreFoto);
		Resource recurso=null;;

		String contentType;
	
		try {
			contentType = Files.probeContentType(rutaArchivo);
			recurso=uploadService.cargar(nombreFoto);
		} catch (IOException e) {
			contentType = "application/octet-stream";
		}

		HttpHeaders cabecera = new HttpHeaders(); 
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).headers(cabecera).body(recurso);
	}
	
	@Secured({ "ROLE_ADMIN" })
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		return clienteService.findAllRegiones();
	}

}
