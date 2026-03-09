package com.cts.jpmc.exception;

public class ItemNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ItemNotFoundException(String msg) {
        super(msg);
    }
}