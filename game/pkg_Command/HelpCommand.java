package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

public class HelpCommand extends Command {

    private int helpLimit = 5;

    private final CommandWords aCommandWord;

    public HelpCommand(CommandWords pCommandWord) {
        this.aCommandWord = pCommandWord;
    }

    @Override
    public boolean execute(Player player, String secondWord) {
        helpLimit--;
        if (helpLimit <= 0) {
            player.getUserInterface().println("Vous avez taper trop de fois la commande help");
            return false;
        }

        player.getUserInterface().println("\nYou are lost. You are alone.\n\nYour command words are:");
        player.getUserInterface().println(this.aCommandWord.getCommandList());
        return true;
    }
}
