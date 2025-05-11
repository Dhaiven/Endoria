package game.pkg_Entity;

import game.pkg_Object.Vector2;

public enum FacingDirection {
    NORTH("north", new Vector2(0, -1)),
    SOUTH("south", new Vector2(0, 1)),
    EAST("east", new Vector2(1, 0)),
    WEST("west", new Vector2(-1, 0));

    private final String name;
    private final Vector2 offset;

    FacingDirection(String name, Vector2 offset) {
        this.name = name;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public Vector2 getOffset(double factor) {
        return new Vector2(offset.x() * factor, offset.y() * factor);
    }

    /**
     * Return vector.x() if pos is EAST or WEST else vector.y()
     */
    public double getVectorComponent(Vector2 vector) {
        if (this.offset.x() != 0) {
            return vector.x();
        }

        return vector.y();
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

    public static FacingDirection fromVectors(Vector2 from, Vector2 to) {
        if (from.x() > to.x()) {
            return FacingDirection.WEST;
        } else if (from.x() < to.x()) {
            return FacingDirection.EAST;
        } else if (from.y() > to.y()) {
            return FacingDirection.NORTH;
        } else if (from.y() < to.y()) {
            return FacingDirection.SOUTH;
        }

        return null;
    }
}
