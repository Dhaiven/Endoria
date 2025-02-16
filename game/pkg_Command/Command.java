package game.pkg_Command;

import game.Player;

/**
 * Classe pkg_Command. Command - une commande du jeu d'aventure Zuul.
 *
 * @author Alban Fournier--Cibray
 */
public abstract class Command {

    private String secondWord = null;

    public String getSecondWord() {
        return secondWord;
    }

    public Command setSecondWord(String secondWord) {
        this.secondWord = secondWord;
        return this;
    }

    abstract public boolean execute(Player player, String secondWord);

}// pkg_Command.Command
