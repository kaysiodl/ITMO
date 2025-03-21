package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NullFieldException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveGreater extends Command {
    private final Console console;
    private CollectionManager collectionManager;

    public RemoveGreater(Console console, CollectionManager collectionManager) {
        super("remove_greater", "delete all items from the collection that exceed the specified item");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError, NullFieldException {
        if (args.length != 1) {
            throw new ExecutionError("This command accepts one argument.");
        }


        if (args[0].isEmpty())
            console.printError("Incorrect number of arguments");
        Integer id = -1;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            console.printError("Id has not correct format.");
        }catch (RuntimeException e) {
            throw new NullFieldException("Id can't be null.");
        }

        Map persons = collectionManager.getCollection();
        if (persons == null || persons.isEmpty()) {
            console.print("No persons to compare.\n");
        }
        List<Integer> personList = collectionManager.sort();
        try{
            for (Integer greaterId: personList){
                if (greaterId > id){
                    collectionManager.removeById(greaterId);
                }
            }
        }catch (Exception e){
            throw new RuntimeException();
        }
        console.print("Persons that are greater than the given element successfully removed.\n");
        return true;
    }
}