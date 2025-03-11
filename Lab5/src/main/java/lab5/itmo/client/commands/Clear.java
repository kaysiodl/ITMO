package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;


public class Clear extends Command {
    private final Console console;
    private CollectionManager collectionManager = new CollectionManager();

    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "clear the collection");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length > 0) {
            throw new ExecutionError("This command does not accept any arguments.");
        }

        return collectionManager.removeAll();
    }
}
