package lab5.itmo.client.commands;

import lab5.itmo.client.io.Controller;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.exceptions.ExecutionError;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ExecuteScript extends Command {
    private final StandartConsole console;
    private final Controller controller;

    public ExecuteScript(StandartConsole console, Controller controller) {
        super("execute_script", "read and execute script from the file");
        this.console = console;
        this.controller = controller;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        try {
            String filePath = args[0];
            String scriptContent = readScriptFile(filePath);
            executeScript(scriptContent, filePath);
        } catch (IndexOutOfBoundsException e) {
            throw new ExecutionError("Type name of the file.");
        }
        return true;
    }

    private String readScriptFile(String path) throws ExecutionError {
        StringBuilder script = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new ExecutionError("File doesn't exist: " + path);
        } catch (IOException e) {
            throw new ExecutionError("Failed reading file: " + path);
        }
        return script.toString();
    }

    private void executeScript(String script, String filePath) throws ExecutionError {
        if (controller.getLaunchedScripts().contains(filePath)) {
            controller.clearLaunchedScripts();
            throw new ExecutionError("Recursive script call: " + filePath);
        }

        controller.addLaunchedScript(filePath);
        List<String> scriptLines = List.of(script.split("\n"));
        console.setScript(scriptLines);
        console.setScriptExecutionMode(true);

        try {
            for (String line : scriptLines) {
                if (!line.trim().isEmpty()) {
                    console.println("Executing: " + line);
                    String result = controller.handleInput(line);
                    console.println(result);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed executing script: " + e.getMessage());
        } finally {
            controller.removeLaunchedScript(filePath);
            console.setScriptExecutionMode(false);
        }
    }
}