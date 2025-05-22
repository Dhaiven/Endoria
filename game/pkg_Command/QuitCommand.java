package game.pkg_Command;

import game.GameEngineV2;
import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class QuitCommand extends Command {

    public QuitCommand() {
        super("quit", "Quitter le jeu");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 0) {
            player.getUserInterface().println("Merci d'avoir jouer");
            GameEngineV2.getInstance().stop();
            return true;
        }
        
        player.getUserInterface().println("Quitter quoi ?");
        return false;
    }
}
