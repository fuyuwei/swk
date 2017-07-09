package com.swk.common.exception;

public class PostingException extends RuntimeException {

	private static final long serialVersionUID = 5969404579580331293L;
	
	private int errorCode;
	
	private String errorMessage;

	public PostingException(int errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	

}
