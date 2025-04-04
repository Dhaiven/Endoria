package game.pkg_Command;

import game.pkg_Entity.Character;
import game.pkg_Entity.Entity;
import game.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class TalkCommand extends Command {

    public TalkCommand() {
        super("talk", "Permet de parler à un PNJ");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println("Parler à qui ?");
            return false;
        }

        for (Entity entity : player.getCurrentRoom().getEntities()) {
            if (entity instanceof Character character) {
                if (character.getName().equals(args[0])) {
                    character.onInteract(player);
                    return true;
                }
            }
        }
        player.getUserInterface().println("Ce personnage n'existe pas");
        return false;
    }
}
