package com.bolsadeideas.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="facturas")
public class Factura implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String descripcion;
	private String observacion;
	
	@Column(name="create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	@JsonIgnoreProperties(value={"facturas","hibernateLazyInitializer","handler"},allowSetters = true) //ignoramos la relacion inversa de cliente
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cliente_id")
	private Cliente cliente;
	
	@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"} )
	@OneToMany(fetch =FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="factura_id")
	private List<ItemFactura> items;
	
	
	public Factura() {
		this.items= new ArrayList<>();
	}
    @PrePersist
	public void prePersist() {
		this.createAt=new Date();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<ItemFactura> getItems() {
		return items;
	}
	

	public void setItems(List<ItemFactura> items) {
		this.items = items;
	}
	
    public Double getTotal() {
    	Double total=0.00;
    	for(ItemFactura item:items) {
    		total += item.getImporte();
    	}
    	return total;
    }
}
