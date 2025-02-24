package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Item.Beamer;
import game.pkg_Item.Item;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class ChargeCommand extends Command {

    public ChargeCommand() {
        super("charge", "Permet de charger le beamer");
    }

    @Override
    public boolean execute(Player player, String[] args) {
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
