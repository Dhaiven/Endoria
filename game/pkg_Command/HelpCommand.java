package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class HelpCommand extends Command {

    private int helpLimit = 5;

    private final CommandManager aCommandWord;

    public HelpCommand(CommandManager pCommandWord) {
        super("help", "Affiche la liste de toutes les commandes disponibles");
        this.aCommandWord = pCommandWord;
    }

    @Override
    public boolean execute(Player player, String[] args) {
        helpLimit--;
        if (helpLimit <= 0) {
            player.getUserInterface().println("Vous avez taper trop de fois la commande help");
            return false;
        }

        player.getUserInterface().println("Voici toutes les commandes disponibles: ");
        for (Command command : this.aCommandWord.getCommands().values()) {
            player.getUserInterface().println(command.getName() + " - " + command.getDescription());
        }
        return true;
    }
}
