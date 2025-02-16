public class FireCommand extends Command {
    
    @Override
    public boolean execute(Player player, String secondWord) {
        Item item = player.getItemList().getItemByName("beamer");
        if (item instanceof Beamer beamer) {
            if (beamer.isFired()) {
                player.getUserInterface().println("Téléportation...");
                beamer.setFired(false);
                player.goRoom(beamer.getFiredRoom());

                beamer.setFiredRoom(null);
                player.getGameEngine().printLocationInfo();
                return true;
            }

            player.getUserInterface().println("Le beamer n'est pas chargé");
            return false;
        }

        player.getUserInterface().println("Vous n'avez pas de beamer à utilisé");
        return false;
    }
}
