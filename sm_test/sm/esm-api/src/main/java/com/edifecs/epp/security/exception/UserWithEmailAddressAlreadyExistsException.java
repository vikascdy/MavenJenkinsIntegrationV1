package com.edifecs.epp.security.exception;

/**
 * Created by willclem on 9/17/2014.
 */
public class UserWithEmailAddressAlreadyExistsException extends SecurityManagerException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "User with email address {0} already exists";
    }

    public UserWithEmailAddressAlreadyExistsException(String email) {
        super(new String[] {email});
    }

    public UserWithEmailAddressAlreadyExistsException(String email, Throwable cause) {
        super(new String[] {email}, cause);
    }

}
