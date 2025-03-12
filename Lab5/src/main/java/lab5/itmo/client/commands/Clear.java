package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;

/**
 * A command that clears the collection.
 */
public class Clear extends Command {
    private final Console console;
    private CollectionManager collectionManager = new CollectionManager();

    /**
     * Constructs a {@code Clear} command.
     *
     * @param console           The console used for input and output.
     * @param collectionManager The collection manager whose collection will be cleared.
     */
    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "clear the collection");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    /**
     * Executes the command by clearing the collection.
     *
     * @param args The arguments passed to the command (should be empty).
     * @return {@code true} if the collection was cleared successfully.
     * @throws ExecutionError If the command is provided with arguments.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length > 0) {
            throw new ExecutionError("This command does not accept any arguments.");
        }

        return collectionManager.removeAll();
    }
}