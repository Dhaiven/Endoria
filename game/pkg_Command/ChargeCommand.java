package game.pkg_Command;

import game.Player;
import game.pkg_Item.Beamer;
import game.pkg_Item.Item;

public class ChargeCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        Item item = player.getItemList().getItemByName("beamer");
        if (item instanceof Beamer beamer) {
            if (!beamer.isFired()) {
                beamer.setFired(true);
                beamer.setFiredRoom(player.getCurrentRoom());
                player.getUserInterface().println("Rechargement !");
                return true;
            }

            player.getUserInterface().println("Le beamer est déjà chargé");
            return false;
        }

        player.getUserInterface().println("Vous n'avez pas de beamer à chargé");
        return false;
    }
}
