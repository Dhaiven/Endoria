package game.pkg_World.loader;

public enum ObjectGroupOrder {
    ROOMS(0, "rooms"),
    DOORS(1, "doors");

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
