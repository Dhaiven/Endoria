package game.pkg_Command;

import game.pkg_Player.Player;

public class ResumeGameCommand extends Command {

    public ResumeGameCommand() {
        super("resume", "Reprend le jeu");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        player.getUserInterface().closeAllOpenedOverlays();
        return false;
    }
}
