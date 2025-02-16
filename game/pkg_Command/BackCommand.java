package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

public class BackCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord != null) {
            player.getUserInterface().println("Cette commande n'accept pas de second paramètre");
            return false;
        }

        if (player.back()) {
            player.getGameEngine().printLocationInfo();
            return true;
        }

        player.getUserInterface().println("Vous ne pouvez pas revenir en arrière");
        return false;
    }
}
