package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.client.CommandManager;

import java.io.IOException;
import java.util.List;

public class History extends Command{
    private final Console console;
    private CommandManager commandManager;

    public History(Console console, CommandManager commandManager){
        super("history", "print the last 13 commands");
        this.commandManager = commandManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        List<Command> commandHistory = commandManager.getHistory();

        if (commandHistory.isEmpty()) {
            try {
                console.print("Command history is empty.\n");
            } catch (Exception e) {
                throw new ExecutionError("Failed to write to console: " + e.getMessage());
            }
            return true;
        }

        int commandsToShow = Math.min(commandHistory.size(), 13);

        StringBuilder historyOutput = new StringBuilder("Last " + commandsToShow + " commands:\n");
        for (int i = commandHistory.size() - commandsToShow; i < commandHistory.size(); i++) {
            historyOutput.append("  ").append(commandHistory.get(i)).append("\n");
        }

        try {
            console.println(historyOutput.toString());
        } catch (IOException e) {
            throw new ExecutionError("Failed to write to console: " + e.getMessage());
        }

        return true;
    }
}
