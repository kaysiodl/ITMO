package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.exceptions.ExecutionError;

/**
 * A command that clears the collection.
 */
public class Clear extends Command {
    private final CollectionManager collectionManager;

    /**
     * Constructs a {@code Clear} command.
     *
     * @param console           The console used for input and output.
     * @param collectionManager The collection manager whose collection will be cleared.
     */
    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "clear the collection");
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by clearing the collection.
     *
     * @return {@code true} if the collection was cleared successfully.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        return collectionManager.removeAll();
    }
}