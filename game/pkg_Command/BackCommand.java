package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class BackCommand extends Command {

    public BackCommand() {
        super("back", "Permet de revenir dans la salle précédente");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 0) {
            player.getUserInterface().println("Cette commande n'accept pas de second paramètre");
            return false;
        }

        if (player.back()) {
            //player.getGameEngine().printLocationInfo();
            return true;
        }

        player.getUserInterface().println("Vous ne pouvez pas revenir en arrière");
        return false;
    }
}
