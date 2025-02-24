package game.pkg_World.loader;

public enum ObjectGroupOrder {
    SPAWNS(0, "spawns"),
    ROOMS(1, "rooms"),
    DOORS(2, "doors");

    private final int order;
    private final String id;

    ObjectGroupOrder(int order, String id) {
        this.order = order;
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public String getId() {
        return id;
    }
}
