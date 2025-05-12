package game.pkg_Tile;

public enum CollisionType {
    IF_SAME_LAYER("if_same_layer"),
    IF_DIFFERENT_LAYER("if_different_layer"),
    IF_ADJACENT_LAYER("if_adjacent_layer"),
    IF_HIGHEST("if_highest"),
    IF_HIGHEST_AND_DIFFERENT_LAYER("if_highest_and_different_layer"),
    ALL_LAYER("all_layer");

    private final String id;

    CollisionType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static CollisionType from(String id) {
        id = id.toLowerCase();
        for (CollisionType type : CollisionType.values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }

        return null;
    }
}
