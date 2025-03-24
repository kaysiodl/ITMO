package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.collection.managers.CollectionManager;

import java.io.IOException;

public class Save extends Command{
    private final CollectionManager collectionManager;

    public Save(CollectionManager collectionManager){
        super("save", "saves collection to a file");
        this.collectionManager = collectionManager;
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
