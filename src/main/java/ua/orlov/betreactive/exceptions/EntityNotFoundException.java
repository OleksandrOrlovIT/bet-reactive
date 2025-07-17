package ua.orlov.betreactive.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) { super(message); }
}