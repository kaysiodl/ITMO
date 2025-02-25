package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;

public class UpdateId extends Command{
    private final Console console;
    private final CollectionManager collectionManager;

    public UpdateId(Console console,CollectionManager collectionManager){
        super("Update id", "update the value of a collection item" +
                " whose id is equal to the specified one");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length != 1) {
            throw new ExecutionError("This command accepts only one argument.");
        }

        Integer id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new ExecutionError("Invalid id format. id must be an integer.");
        }

        //Person personToUpdate = collectionManager.getCollection().get(id);
        //if (personToUpdate == null) {
       //     throw new ExecutionError("Element with ID " + id + " not found.");
       // }
        return true;
    }
}
