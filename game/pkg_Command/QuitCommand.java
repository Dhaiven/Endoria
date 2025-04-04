package game.pkg_Command;

import game.GameEngineV2;
import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class QuitCommand extends Command {

    public QuitCommand() {
        super("quit", "Stop le jeu");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 0) {
            player.getUserInterface().println("Thank you for playing.  Good bye.");
            GameEngineV2.getInstance().stop();
            return true;
        }
        
        player.getUserInterface().println("Quit what?");
        return false;
    }
}
