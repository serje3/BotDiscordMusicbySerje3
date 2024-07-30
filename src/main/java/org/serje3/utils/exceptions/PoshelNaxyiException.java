package org.serje3.utils.exceptions;

public class PoshelNaxyiException extends Exception{
    public PoshelNaxyiException() {
        super("Иди нахуй");
    }

    public PoshelNaxyiException(String message) {
        super("Иди нахуй. Причина: " + message);
    }
}
