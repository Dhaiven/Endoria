package game.pkg_Entity;

import game.pkg_Object.Vector2;

import java.util.Vector;

public enum FacingDirection {
    NORTH("north", new Vector2(0, -1)),
    SOUTH("south", new Vector2(0, 1)),
    EAST("east", new Vector2(1, 0)),
    WEST("west", new Vector2(-1, 0));

    private final String name;
    private final Vector2 vector;

    FacingDirection(String name, Vector2 vector) {
        this.name = name;
        this.vector = vector;
    }

    public String getName() {
        return name;
    }

    public Vector2 getVector() {
        return vector;
    }

    public FacingDirection getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public static FacingDirection from(String name) {
        name = name.toLowerCase();
        for (FacingDirection facingDirection : FacingDirection.values()) {
            if (facingDirection.getName().equals(name)) {
                return facingDirection;
            }
        }

        return null;
    }
}
