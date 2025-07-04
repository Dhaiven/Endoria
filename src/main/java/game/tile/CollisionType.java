package game.tile;

public enum CollisionType {
    IF_SAME_LAYER("if_same_layer"),
    IF_DIFFERENT_LAYER("if_different_layer"),
    IF_ADJACENT_LAYER("if_adjacent_layer"),
    IF_HIGHEST("if_highest"),
    IF_HIGHEST_SUBLAYER("if_highest_sublayer"),
    IF_HIGHEST_AND_DIFFERENT_LAYER("if_highest_and_different_layer"),
    ALL_LAYER("all_layer");

    private final String id;

    CollisionType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
