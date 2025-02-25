package lab5.itmo.client.io;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.Command;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NotFoundCommandException;

import java.util.Arrays;

public class Controller {
    private final StandartConsole console;
    private final CommandManager commandManager;

    public Controller(CommandManager commandManager, StandartConsole console) {
        this.console = console;
        this.commandManager = commandManager;
    }

    public void run() {
        String input;
        try {
            while (true) {
                console.print("Enter the command: ");
                input = console.read();
                if (input == null) {
                    break;
                }
                try {
                    String result = handleInput(input);
                    console.println(result);
                } catch (Exception e) {
                    console.println("Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            console.println("Error: " + e.getMessage());
        }
    }

    public String handleInput(String input) {
        try {
            boolean isSuccess = parseInput(input);
            return input + (isSuccess ? " successfully" : " unsuccessfully") + " completed";
        } catch (ExecutionError e) {
            return "Execution error: " + e.getMessage();
        } catch (NotFoundCommandException e) {
            return "Error: " + e.getMessage() + " Type help to get info about commands.";
        }
    }

    private boolean parseInput(String input) throws ExecutionError, NotFoundCommandException {
        String[] data = input.split(" ");
        String commandName = data[0];
        Command command = commandManager.getCommand(commandName);
        if (command == null) {
            throw new NotFoundCommandException("Command '" + commandName + "' is not found.");
        }
        commandManager.addToHistory(command);
        return command.apply(Arrays.copyOfRange(data, 1, data.length));
    }
}