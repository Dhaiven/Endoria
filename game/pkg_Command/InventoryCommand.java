package game.pkg_Command;

import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class InventoryCommand extends Command {

    public InventoryCommand() {
        super("inventory", "Affiche tous le contenue de l'inventaire");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        player.getUserInterface().println("Inventaire: ");
        player.getUserInterface().println(player.getItemList().getItemString());
        return true;
    }
}
