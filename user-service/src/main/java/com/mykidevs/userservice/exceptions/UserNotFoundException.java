package com.mykidevs.userservice.exceptions;

public class UserNotFoundException extends RuntimeException {
public UserNotFoundException(String m) {
    super(m);
}
}
