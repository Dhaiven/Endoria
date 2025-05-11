package game.pkg_Tile;

public enum TileProperty {
    CHANGE_LAYER("changeLayer");

    private final String id;

    TileProperty(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
