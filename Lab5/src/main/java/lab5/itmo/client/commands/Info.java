package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class Info extends Command{
    private final Console console;
    private CollectionManager collectionManager = new CollectionManager();

    public Info(Console console, CollectionManager collectionManager){
        super("info", "output information about the collection (type, initialization date, number of items, etc.)");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        String collectionType = collectionManager.getCollection().getClass().getSimpleName();
        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        int collectionSize = collectionManager.getCollection().size();

        String info = String.format(
                "Collection information:\n" +
                        "  Type: %s\n" +
                        "  Initialization date: %s\n" +
                        "  Number of elements: %d\n",
                collectionType, lastInitTime, collectionSize
        );

        try {
            console.println(info);
        } catch (IOException e) {
            throw new ExecutionError("Failed to write to console: " + e.getMessage());
        }

        return true;
    }
}
