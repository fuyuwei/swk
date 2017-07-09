package com.swk.common.enums;

public enum CommonPostCode {
	
	PARAM_NULL(-1,"param required"),
	PARAM_LENGTH(-2,"param too long");
	
	private int errorCode;
	
	private String errorMesage;
	

	private CommonPostCode(int errorCode, String errorMesage) {
		this.errorCode = errorCode;
		this.errorMesage = errorMesage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMesage() {
		return errorMesage;
	}

	public void setErrorMesage(String errorMesage) {
		this.errorMesage = errorMesage;
	}

	
}
