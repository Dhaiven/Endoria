package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class InventoryCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        player.getUserInterface().println("Inventaire: ");
        player.getUserInterface().println(player.getItemList().getItemString());
        return true;
    }
}
