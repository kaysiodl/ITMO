package lab5.itmo.client.commands;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.exceptions.ExecutionError;

import java.io.IOException;
import java.util.List;

/**
 * A command that lists all available commands and their descriptions.
 */
public class Help extends Command {
    private final Console console;
    private final CommandManager commandManager;

    /**
     * Constructs a {@code Help} command.
     *
     * @param console        The console used for input and output.
     * @param commandManager The command manager providing the list of commands.
     */
    public Help(Console console, CommandManager commandManager) {
        super("help", "list of all available commands");
        this.commandManager = commandManager;
        this.console = console;
    }

    /**
     * Executes the command by printing the list of available commands and their descriptions.
     *
     * @param args The arguments passed to the command (not used).
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If an error occurs during execution.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        List<Command> commands = commandManager.getCommands();
        for (Command command : commands) {
            try {
                console.println(command.getName() + " - " + command.getDescription());
            } catch (IOException e) {
                throw new ExecutionError();
            }
        }
        return true;
    }
}