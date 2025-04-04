package game.pkg_Command;

import game.pkg_Player.Player;

/**
 * Classe pkg_Command. Command - une commande du jeu d'aventure Zuul.
 *
 * @author Alban Fournier--Cibray
 */
public abstract class Command {

    private String secondWord = null;

    private final String name;
    private final String description;

    public Command(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public Command setSecondWord(String secondWord) {
        this.secondWord = secondWord;
        return this;
    }

    abstract public boolean execute(Player player, String[] args);

}// pkg_Command.Command
