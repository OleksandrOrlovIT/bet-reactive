package ua.orlov.betreactive.model;

public enum EventStatus {

    ACTIVE, EXPIRED;

    public static EventStatus fromStringIgnoreCase(String string) {
        return switch (string.toUpperCase()) {
            case "ACTIVE" -> EventStatus.ACTIVE;
            case "EXPIRED" -> EventStatus.EXPIRED;
            default -> throw new IllegalStateException("Unknown EventStatus: " + string);
        };
    }

}
