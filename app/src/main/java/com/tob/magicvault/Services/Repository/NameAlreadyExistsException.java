package com.tob.magicvault.Services.Repository;

public class NameAlreadyExistsException extends Exception {
    NameAlreadyExistsException(String name) {
        super("A magic trick with the name : " + name + " already exists");
    }
}
