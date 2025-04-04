package game.pkg_Command;

import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class DropCommand extends Command {

    public DropCommand() {
        super("drop", "Permet de dropper un item de son inventaire");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println("Drop what?");
            return false;
        }

        if (player.drop(args[0])) {
            //player.getGameEngine().printLocationInfo();
            return true;
        }

        player.getUserInterface().println("Vous ne pouvez pas prendre cet item");
        return false;
    }
}
