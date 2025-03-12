package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;

import java.io.IOException;
import java.nio.file.Path;

public class Save extends Command{
    private final Console console;
    private CollectionManager collectionManager;

    public Save(Console console, CollectionManager collectionManager){
        super("save", "saves collection to a file");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        try {
            collectionManager.saveCollection();
        } catch (IOException e) {
            throw new ExecutionError(e.getMessage());
        }
        return true;
    }
}
