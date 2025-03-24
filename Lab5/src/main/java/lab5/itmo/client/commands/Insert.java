package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NullFieldException;

import java.util.Arrays;

public class Insert extends Command {
    private final StandartConsole console;
    private final CollectionManager collectionManager;

    public Insert(StandartConsole console, CollectionManager collectionManager) {
        super("insert", "add a new item with the specified key");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError, NullFieldException {
        if (args.length == 0) {
            console.printError("Incorrect number of arguments: id is required.");
            return false;
        }
        Integer key;
        try {
            key = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            console.printError("Key must be a valid integer.");
            return false;
        }

        if (collectionManager.getCollection().containsKey(key)) {
            console.printError("An element with this key already exists.");
            return false;
        }

        try {
            if (console.isScriptExecutionMode()) {
                String[] data = Arrays.copyOfRange(args, 1, args.length);
                Person person = AskManager.personFromScript(data);
                person.validate();
                collectionManager.add(person, key);
            } else {
                Person person = AskManager.askPerson(console);
                if (person != null) {
                    collectionManager.add(person, key);
                }
            }

        } catch (ExecutionError e) {
            throw new ExecutionError(e.getMessage());
        } catch (AskManager.Break e) {
            throw new RuntimeException(e);
        }
        return true;

    }
}