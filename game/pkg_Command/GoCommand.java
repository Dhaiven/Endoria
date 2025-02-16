package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class GoCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord == null) {
            player.getUserInterface().println("Go where ?");
            return false;
        }

        if (player.goRoom(secondWord)) {
            player.getGameEngine().printLocationInfo();
        } else {
            player.getUserInterface().println("There is no door !");
        }

        return true;
    }
}
