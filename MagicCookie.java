public class MagicCookie extends Item {

    public MagicCookie() {
        super("cookie", "Un cookie magique qui augmente la poids max", 5);
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        player.setMaxWeight(100);
    }
}
