package game.pkg_Command;

import game.pkg_Player.Player;

public class PauseGameCommand extends Command {

    public PauseGameCommand() {
        super("pause", "Stop le jeu");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        player.getUserInterface().getPauseOverlay().setVisible(true);
        player.getUserInterface().print("Le jeu est en pause");
        return false;
    }
}