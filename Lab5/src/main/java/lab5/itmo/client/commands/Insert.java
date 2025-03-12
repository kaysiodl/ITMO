package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NullFieldException;

import java.io.IOException;
import java.util.List;

public class Insert extends Command {
    private final StandartConsole console;
    private final CollectionManager collectionManager;
    private final AskManager askManager;

    public Insert(StandartConsole console, CollectionManager collectionManager, AskManager askManager) {
        super("insert", "add a new item with the specified key");
        this.console = console;
        this.collectionManager = collectionManager;
        this.askManager = askManager;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError, NullFieldException {
        try {
            if (args.length != 1) {
                throw new ExecutionError("This command accepts one argument.");
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

            if (console.isScriptExecutionMode()) {
                List<String> scriptData = console.getScriptData();
                scriptData.removeFirst();
                if (scriptData.size() < 11) {
                    throw new ExecutionError("Not enough data in script for 'insert' command.");
                }

                console.setScript(scriptData);

                Person newPerson = AskManager.askPerson(console);
                if (newPerson == null) {
                    console.printError("Failed to create a new Person from script data.");
                    return false;
                }


                collectionManager.add(newPerson, key);
                console.println("New element added successfully with key: " + key);
            } else {
                Person newPerson = AskManager.askPerson(console);
                if (newPerson == null) {
                    console.printError("Failed to create a new Person.");
                    return false;
                }

                collectionManager.add(newPerson, key);
                console.println("New element added successfully with key: " + key);
            }

            return true;
        } catch (AskManager.Break e) {
            throw new RuntimeException(e);
        }
    }
}