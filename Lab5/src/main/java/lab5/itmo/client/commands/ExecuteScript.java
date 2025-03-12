package lab5.itmo.client.commands;

import lab5.itmo.client.io.Controller;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.exceptions.ExecutionError;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * A command that reads and executes a script from a file.
 */
public class ExecuteScript extends Command {
    private final StandartConsole console;
    private final Controller controller;

    /**
     * Constructs an {@code ExecuteScript} command.
     *
     * @param console    The console used for input and output.
     * @param controller The controller used to manage script execution.
     */
    public ExecuteScript(StandartConsole console, Controller controller) {
        super("execute_script", "read and execute script from the file");
        this.console = console;
        this.controller = controller;
    }

    /**
     * Executes the command by reading and executing the script from the specified file.
     *
     * @param args The arguments passed to the command (should contain one argument: file path).
     * @return {@code true} if the script executed successfully.
     * @throws ExecutionError If the file path is missing or invalid.
     */
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

    /**
     * Reads the script file and returns its content as a string.
     *
     * @param path The path to the script file.
     * @return The content of the script file as a string.
     * @throws ExecutionError If the file does not exist or cannot be read.
     */
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

    /**
     * Executes the script by processing its commands.
     *
     * @param script   The content of the script as a string.
     * @param filePath The path to the script file.
     * @throws ExecutionError If a recursive script call is detected or an error occurs during execution.
     */
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
            for (int i = 0; i < scriptLines.size(); i++) {
                String line = scriptLines.get(i).trim();
                if (!line.trim().isEmpty()) {
                    String[] data = line.split(" ");
                    if (data[0].equals("insert") || data[0].equals("update")) {
                        console.println("Executing: " + line);
                        String result = controller.handleInput(line);
                        console.println(result);
                        i += 11;
                    } else {
                        console.println("Executing: " + line);
                        String result = controller.handleInput(line);
                        console.println(result);
                    }
                }
            }
        } catch (Exception e) {
            throw new ExecutionError(e.getMessage());
        } finally {
            controller.removeLaunchedScript(filePath);
            console.setScriptExecutionMode(false);
        }
    }
}