package com.github.lianick.exception;

public class EnumNotFoundException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnumNotFoundException(String message) {
		super(message);
	}
}
