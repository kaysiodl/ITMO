package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.client.CommandManager;

import java.io.IOException;
import java.util.List;

public class Help extends Command{
    private final Console console;
    private CommandManager commandManager = new CommandManager();

    public Help(Console console, CommandManager commandManager){
        super("help", "list of all available commands");
        this.commandManager = commandManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        List<Command> commands = commandManager.getCommands();
        for (Command command: commands){
            try{
                console.println(command.getName() + " - " + command.getDescription());
            }catch (IOException e){
                throw new ExecutionError();
            }
        }
        return true;
    }
}
