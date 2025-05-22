package game.pkg_Command;

import java.util.HashMap;

/**
 * @author  DEBELLE Hugo
 * @version 3.0
 */
public class CommandManager
{

    private final CommandExecutor commandExecutor;
    private final HashMap<String, Command> aCommands = new HashMap<>();

    public CommandManager() {
        this.commandExecutor = new CommandExecutor(this);

        this.register(new AleaCommand());
        this.register(new BackCommand());
        this.register(new GoCommand());
        this.register(new HelpCommand(this));
        this.register(new QuitCommand());
        this.register(new DropCommand());
        this.register(new InventoryCommand());
        this.register(new TakeCommand());
        this.register(new TestCommand());
        this.register(new LookCommand());
        this.register(new PauseGameCommand());
        this.register(new ResumeGameCommand());
        this.register(new EatCommand());
        this.register(new FireCommand());
        this.register(new ChargeCommand());
        this.register(new TalkCommand());
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public HashMap<String, Command> getCommands() {
        return aCommands;
    }

    public Command getCommand(String commandName) {
        return aCommands.get(commandName);
    }

    public void register(Command command) {
        if (this.aCommands.containsKey(command.getName())) {
            throw new IllegalArgumentException("Command " + command.getName() + " is already registered!");
        }

        this.aCommands.put(command.getName(), command);
    }
}
