public class Door {

    private final Room to;

    public Door(Room to) {
        this.to = to;
    }

    public Room getTo() {
        return to;
    }

    public boolean canPass(Player player) {
        return true;
    }
}
