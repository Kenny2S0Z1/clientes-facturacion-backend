package com.bolsadeideas.springboot.backend.apirest.models.response;

public class ResponseErrorForm {

	private String field;
	private String messageError;
	
	
	public ResponseErrorForm(String field, String messageError) {
		super();
		this.field = field;
		this.messageError = messageError;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessageError() {
		return messageError;
	}
	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}
	
}
