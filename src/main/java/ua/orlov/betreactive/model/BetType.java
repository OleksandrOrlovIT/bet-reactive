package ua.orlov.betreactive.model;

public enum BetType {

    WIN, LOSE, DRAW;

    public static BetType fromStringIgnoreCase(String string) {
        return switch (string.toUpperCase()) {
            case "WIN" -> BetType.WIN;
            case "LOSE" -> BetType.LOSE;
            case "DRAW" -> BetType.DRAW;
            default -> throw new IllegalStateException("Unknown BetType: " + string);
        };
    }

}
