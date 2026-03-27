package com.mykidevs.userservice.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
public UserAlreadyExistsException(String m) {
    super(m);
}
}
