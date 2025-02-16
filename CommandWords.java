import java.util.HashMap;

/**
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 * 
 * This class holds an enumeration table of all command words known to the game.
 * It is used to recognise commands as they are typed in.
 *
 * @author  Michael Kolling and David J. Barnes + D.Bureau
 * @version 2008.03.30 + 2019.09.25
 */
public class CommandWords
{
    private final HashMap<String, CommandWord> aValidCommands = new HashMap();

    public CommandWords() {
        for (CommandWord command : CommandWord.values()) {
            aValidCommands.put(command.name().toLowerCase(), command);
        }
    }

    /**
     * Find the CommandWord associated with a command word.
     * @param commandWord The word to look up.
     * @return The CommandWord correspondng to commandWord, or UNKNOWN
     *         if it is not a valid command word.
     */
    public CommandWord getCommandWord(String commandWord) {
        CommandWord command = this.aValidCommands.get(commandWord);
        if (command != null) {
            return command;
        }

        return CommandWord.UNKNOWN;
    }

    /**
     * Print la liste des commandes disponibles
     */
    public String getCommandList() {
        StringBuilder result = new StringBuilder();
        for (String command : this.aValidCommands.keySet()) {
            result.append(command).append(" ");
        }
        return result.toString();
    }
} // CommandWords
