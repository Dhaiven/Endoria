package game.pkg_Command;

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
            player.getUserInterface().enable(false );
            return true;
        }
        
        player.getUserInterface().println("Quit what?");
        return false;
    }
}
