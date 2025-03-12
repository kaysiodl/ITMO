package lab5.itmo.client.io;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.Command;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NotFoundCommandException;
import lab5.itmo.exceptions.NullFieldException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
    private final StandartConsole console;
    private final CommandManager commandManager;
    private List<String> launchedScripts = new ArrayList<>();

    public Controller(CommandManager commandManager, StandartConsole console) {
        this.console = console;
        this.commandManager = commandManager;
    }

    public void addLaunchedScript(String name) {
        launchedScripts.add(name);
    }

    public List<String> getLaunchedScripts() {
        return launchedScripts;
    }

    public void clearLaunchedScripts() {
        this.launchedScripts.clear();
    }

    public void removeLaunchedScript(String filePath) {
        launchedScripts.remove(filePath);
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

    public String handleInput(String input){
        try {
            boolean isSuccess = parseInput(input);
            return input + (isSuccess ? " successfully" : " unsuccessfully") + " completed";
        } catch (ExecutionError e) {
            return "Execution error: " + e.getMessage();
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
        String[] args = data.length > 1 ? Arrays.copyOfRange(data, 1, data.length) : new String[0];
        try {
            return command.apply(args);
        } catch (NullFieldException e) {
            throw new ExecutionError(e.getMessage());
        }
    }
}