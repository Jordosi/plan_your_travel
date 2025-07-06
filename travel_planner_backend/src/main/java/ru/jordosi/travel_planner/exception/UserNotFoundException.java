package ru.jordosi.travel_planner.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User not found " + message);
    }

    public UserNotFoundException(Long id) {
        super(String.format("User with id %d not found", id));
    }
}
