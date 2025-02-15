public class LockDoor extends Door {

    private final Item key;

    public LockDoor(Room from, Item key) {
        super(from);
        this.key = key;
    }

    @Override
    public boolean canPass(Player player) {
        Item playerKey = player.getItemList().getItemByName(key.getName());
        if (playerKey != null) {
            player.use(playerKey);
            return true;
        }

        return false;
    }
}
