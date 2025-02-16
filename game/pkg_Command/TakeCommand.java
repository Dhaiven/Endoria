package game.pkg_Command;

import game.Player;

public class TakeCommand extends Command {
    
    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord == null) {
            player.getUserInterface().println("Take what?");
            return false;
        }
        
        if (player.take(secondWord)) {
            player.getGameEngine().printLocationInfo();
            return true;
        }

        player.getUserInterface().println("Vous ne pouvez pas prendre cet item");
        return false;
    }
}
