package lab5.itmo.client.io.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A standard implementation of the {@link Console} interface for console interaction.
 * Supports both interactive mode and script execution mode.
 */
public class StandartConsole implements Console {
    private boolean scriptExecutionMode = false;
    private static Scanner scanner = new Scanner(System.in);
    private List<String> script = new ArrayList<>();

    /**
     * Sets the script execution mode.
     *
     * @param scriptExecutionMode {@code true} to enable script execution mode, {@code false} to disable it.
     */
    public void setScriptExecutionMode(boolean scriptExecutionMode) {
        this.scriptExecutionMode = scriptExecutionMode;
    }

    /**
     * Checks if the script execution mode is enabled.
     *
     * @return {@code true} if script execution mode is enabled, otherwise {@code false}.
     */
    public boolean isScriptExecutionMode() {
        return scriptExecutionMode;
    }

    /**
     * Retrieves the script data.
     *
     * @return A list of strings representing the script lines.
     */
    public List<String> getScriptData() {
        return script;
    }

    /**
     * Sets the script data.
     *
     * @param script A list of strings representing the script lines to be executed.
     */
    public void setScript(List<String> script) {
        this.script.addAll(script);
    }

    /**
     * Prints an object to the console followed by a newline.
     *
     * @param obj The object to be printed.
     */
    @Override
    public void println(Object obj) {
        System.out.println(obj);
    }

    /**
     * Prints an object to the console without a newline.
     *
     * @param object The object to be printed.
     */
    @Override
    public void print(Object object) {
        System.out.print(object);
    }

    /**
     * Reads a line of input from the console or script.
     *
     * @return The input line as a string.
     * @throws NoSuchElementException If no input is available.
     * @throws IllegalStateException  If the scanner is closed or otherwise unavailable.
     */
    @Override
    public String read() throws NoSuchElementException, IllegalStateException {
        if (!scriptExecutionMode) {
            return scanner.nextLine();
        } else {
            if (script.size() == 1) setScriptExecutionMode(false);
            String line = script.getFirst();
            script.removeFirst();
            println(line);
            return line;
        }
    }

    /**
     * Prints an error message to the console.
     *
     * @param error The error message to be printed.
     */
    @Override
    public void printError(String error) {
        System.out.println(error);
    }
}