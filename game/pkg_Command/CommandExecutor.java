package game.pkg_Command;

import game.pkg_Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommandExecutor {

    private final CommandManager commandManager;
    private String alea;

    public CommandExecutor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * @return un String si il y a un alea else null
     */
    public String getAlea() {
        return alea;
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    public void interpretCommand(final Player player, final String pCommandLine) {
        this.interpretCommand(player, pCommandLine, false);
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     * @param inTest true si la commande est exécuté par la commande test else false
     */
    public void interpretCommand(Player player, final String pCommandLine, boolean inTest) {
        StringTokenizer tokenizer = new StringTokenizer(pCommandLine);
        if (!tokenizer.hasMoreTokens()) {
            return;
        }

        player.getUserInterface().println( "> " + pCommandLine);

        Command vCommand = this.commandManager.getCommand(tokenizer.nextToken());
        if (vCommand == null) {
            player.getUserInterface().println("La commande n'existe pas.");
        } else {
            player.onExecuteCommand(vCommand, this.getParams(tokenizer));
        }
    }

    private String[] getParams(StringTokenizer pCommandLine) {
        List<String> params = new ArrayList<>();
        while (pCommandLine.hasMoreTokens()) {
            params.add(pCommandLine.nextToken());
        }

        return params.toArray(new String[0]);
    }
}
