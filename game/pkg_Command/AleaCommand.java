package game.pkg_Command;

import game.GameEngineV2;
import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class AleaCommand extends Command {

    public AleaCommand() {
        super("alea", "Permet de controler l'aléatoire des transporte rooms");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println("Il faut le nom de la room");
            return false;
        }

        GameEngineV2.getInstance().setAlea(args[0]);

        player.getUserInterface().println("L'aléa a été changé");
        return false;
    }
}