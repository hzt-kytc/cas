package com.kytc.cas.exception;

import javax.security.auth.login.AccountLockedException;

public class AccountNotFoundException extends AccountLockedException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a AccountLockedException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public AccountNotFoundException() {
        super();
    }

    /**
     * Constructs a AccountLockedException with the specified
     * detail message. A detail message is a String that describes
     * this particular exception.
     *
     * <p>
     *
     * @param msg the detail message.
     */
    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
