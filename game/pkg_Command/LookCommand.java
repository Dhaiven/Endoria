package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Item.Item;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class LookCommand extends Command {

    public LookCommand() {
        super("look", "Afficher les infos de la room ou de l'item passé en paramètre");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println(player.getCurrentRoom().getLongDescription());
            return true;
        }

        Item actualItem = player.getCurrentRoom().getItemList().getItemByName(args[0]);
        if (actualItem != null) {
            player.getUserInterface().println(actualItem.getLongDescription());
            return true;
        }

        player.getUserInterface().println("I don't know how to look at something in particular yet.");
        return true;
    }
}
