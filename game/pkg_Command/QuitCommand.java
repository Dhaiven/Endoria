package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class QuitCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord != null) {
            player.getUserInterface().println("Thank you for playing.  Good bye.");
            player.getUserInterface().enable(false );
            return true;
        }
        
        player.getUserInterface().println( "Quit what?" );
        return false;
    }
}
