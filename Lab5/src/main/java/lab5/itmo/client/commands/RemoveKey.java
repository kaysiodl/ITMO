package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.exceptions.ExecutionError;


public class RemoveKey extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemoveKey(Console console, CollectionManager collectionManager) {
        super("remove_key", "delete an item from the collection by its key");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length != 1 || args[0].trim().isEmpty()) {
            throw new ExecutionError("This command accepts one argument. Try again.");
        }

        Integer id;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            console.printError("Id must be a valid integer.");
            return false;
        }

        try {
            if(collectionManager.getById(id) == null){
                console.printError("Can't find this id in collection.");
                return false;
            }
            collectionManager.removeById(id);
            console.print("Element with id " + id + " removed successfully.\n");
            return true;
        } catch (IllegalStateException e) {
            console.printError("Can't find this id in collection.");
            return false;
        }
    }
}