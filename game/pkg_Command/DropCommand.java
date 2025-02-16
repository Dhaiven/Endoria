package game.pkg_Command;

import game.Player;

public class DropCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord == null) {
            player.getUserInterface().println("Drop what?");
            return false;
        }

        if (player.drop(secondWord)) {
            player.getGameEngine().printLocationInfo();
            return true;
        }

        player.getUserInterface().println("Vous ne pouvez pas prendre cet item");
        return false;
    }
}
