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
    private final HashMap<CommandWord, Command> aCommands = new HashMap();

    public CommandWords() {
        for (CommandWord command : CommandWord.values()) {
            aValidCommands.put(command.name().toLowerCase(), command);
        }

        this.aCommands.put(CommandWord.BACK, new BackCommand());
        this.aCommands.put(CommandWord.GO, new GoCommand());
        this.aCommands.put(CommandWord.HELP, new HelpCommand(this));
        this.aCommands.put(CommandWord.QUIT, new QuitCommand());
        this.aCommands.put(CommandWord.DROP, new DropCommand());
        this.aCommands.put(CommandWord.INVENTORY, new InventoryCommand());
        this.aCommands.put(CommandWord.TAKE, new TakeCommand());
        this.aCommands.put(CommandWord.TEST, new TestCommand());
        this.aCommands.put(CommandWord.LOOK, new LookCommand());
        this.aCommands.put(CommandWord.EAT, new EatCommand());
        this.aCommands.put(CommandWord.FIRE, new FireCommand());
        this.aCommands.put(CommandWord.CHARGE, new ChargeCommand());
    }

    /**
     * Find the CommandWord associated with a command word.
     * @param commandWord The word to look up.
     * @return The Command correspondng to commandWord, or null
     *         if it is not a valid command word.
     */
    public Command getCommand(String commandWord) {
        CommandWord command = this.aValidCommands.get(commandWord);
        if (command != null) {
            return this.aCommands.get(command);
        }

        return null;
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
