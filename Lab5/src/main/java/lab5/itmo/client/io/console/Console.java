package lab5.itmo.client.io.console;

import java.io.IOException;

/**
 * An interface for console interaction, providing methods for printing and reading input.
 */
public interface Console {
    /**
     * Prints an object to the console followed by a newline.
     *
     * @param obj The object to be printed.
     * @throws IOException If an I/O error occurs.
     */
    void println(Object obj) throws IOException;

    /**
     * Prints an object to the console without a newline.
     *
     * @param object The object to be printed.
     */
    void print(Object object);

    /**
     * Reads a line of input from the console.
     *
     * @return The input line as a string.
     */
    String read();

    /**
     * Prints an error message to the console.
     *
     * @param error The error message to be printed.
     */
    void printError(String error);
}