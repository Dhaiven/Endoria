package game.pkg_Entity;

public enum FacingDirection {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west");

    private final String name;

    FacingDirection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
