package game.pkg_Command;

import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class TakeCommand extends Command {

    public TakeCommand() {
        super("take", "Prend un objet présent dans la salle");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println("Take what?");
            return false;
        }
        
        if (player.take(args[0])) {
            //player.getGameEngine().printLocationInfo();
            return true;
        }

        player.getUserInterface().println("Vous ne pouvez pas prendre cet item");
        return false;
    }
}
